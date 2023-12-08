package edu.uoc.jjerezt.translateocr.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dalvik.system.DexClassLoader
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentHomeBinding
import edu.uoc.jjerezt.translateocr.runtime.Asset
import edu.uoc.jjerezt.translateocr.runtime.DataStoreManager
import edu.uoc.jjerezt.translateocr.runtime.OfflineServiceProvider
import edu.uoc.jjerezt.translateocr.runtime.dict.Language
import edu.uoc.jjerezt.translateocr.runtime.ocr.TesseractRecognition
import edu.uoc.jjerezt.translateocr.runtime.ocr.Training
import edu.uoc.jjerezt.translateocr.runtime.text.ApertiumTranslator
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // https://developer.android.com/training/basics/intents/result
    private lateinit var mediaFile : String
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            mediaFile = uri.path!!
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private var cameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()) { success ->
        if(success){
            println(success)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val translateButton: Button = root.findViewById(R.id.button)
        val origText : EditText = root.findViewById(R.id.editTextTextMultiLine)
        val destText: TextView = root.findViewById(R.id.editTextTextMultiLine2)

        origLanguage = root.findViewById(R.id.spinner);
        // https://developer.android.com/develop/ui/views/components/spinner
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            root.context,
            R.array.orig_languages_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            origLanguage.adapter = adapter
        }

        origLanguage.onItemSelectedListener = this
        destLanguage = root.findViewById(R.id.spinner2)
        // https://stackoverflow.com/questions/14569296/setting-the-value-of-the-spinner-dynamically
        // Create an ArrayAdapter using the string array and a default spinner layout.
        val dataAdapter = ArrayAdapter<String>(root.context, android.R.layout.simple_spinner_dropdown_item)
        val itemNames: Array<String> = root.context.resources.getStringArray(R.array.dest_languages_array)
        destLanguage.adapter = Language().getAdapterBasedOnOrig(dataAdapter, itemNames, origLanguage.selectedItem.toString())

        // https://stackoverflow.com/questions/71157667/how-to-change-default-input-language-in-textinputedittext-java-android
        // https://stackoverflow.com/questions/56387603/how-we-can-specify-input-language-for-specific-edittextnot-for-whole-app-any-h
        // https://stackoverflow.com/questions/5715072/change-android-keyboard-language
        // https://developer.android.com/reference/android/widget/TextView#setImeHintLocales(android.os.LocaleList)
        val locale = Language().getLocale(origLanguage.selectedItem.toString())
        origText.imeHintLocales = LocaleList(Locale(locale[0], locale[1]))
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.restartInput(origText)

        translateButton.setOnClickListener {
            println("Translate button pressed")
            val textSortida = translate(root.rootView, origLanguage.selectedItem.toString(), destLanguage.selectedItem.toString(), origText.text.toString())
            // https://stackoverflow.com/questions/2116162/how-to-display-html-in-textview
            val spanned = HtmlCompat.fromHtml(textSortida, HtmlCompat.FROM_HTML_MODE_COMPACT)
            destText.text = spanned;
        }

        val img: Button = root.findViewById(R.id.image)
        img.setOnClickListener {
            val imgTest = getImage(root.context)
            val language = Training().getCode(origLanguage.selectedItem.toString())
            val dataPath = Training().copyLanguage(language, root.context)
            val result = TesseractRecognition().recognize(imgTest, dataPath, language)
            origText.setText(result)
        }

        val camera: Button = root.findViewById(R.id.camera)
        camera.setOnClickListener {
            val picture = takeImage(root.context)
            val language = Training().getCode(origLanguage.selectedItem.toString())
            val dataPath = Training().copyLanguage(language, root.context)
            val result = TesseractRecognition().recognize(picture, dataPath, language)
            origText.setText(result)
        }

        return root
    }

    private lateinit var destLanguage: Spinner
    private lateinit var origLanguage: Spinner

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getImage(context: Context): File {
        mediaFile = Asset().copyAssetToCache(context, "hello_world.png").absolutePath
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        pickMedia.launch(PickVisualMediaRequest())
        return File(mediaFile)
    }

    // https://developer.android.com/training/camera/camera-intents
    // https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
    // https://stackoverflow.com/questions/61941959/activityresultcontracts-takepicture
    // https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
    // https://stackoverflow.com/questions/42516126/fileprovider-illegalargumentexception-failed-to-find-configured-root
    // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
    private fun takeImage(context: Context): File {
        mediaFile = Asset().copyAssetToCache(context, "hello_world.png").absolutePath
        // Attempt to allocate a file to store the photo
        val newFile = File(context.cacheDir, "snap.jpg")
        val photoUri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            newFile
        )
        try {
            cameraResultLauncher.launch(photoUri);
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
        mediaFile = newFile.absolutePath
        return File(mediaFile)
    }

    private fun translate(
        view: View,
        origLanguage: String,
        destLanguage: String,
        text: String
    ): String {
        var markUnknown = false
        var markAmbiguity = false

        runBlocking {
            withTimeoutOrNull(2000) {
                markUnknown = DataStoreManager().readValue(
                    view.context,
                    DataStoreManager.markUnknown
                ) ?: false
            }
        }

        runBlocking {
            withTimeoutOrNull(2000) {
                markAmbiguity = DataStoreManager().readValue(
                    view.context,
                    DataStoreManager.markAmbiguity
                ) ?: false
            }
        }

        println("Unknown: $markUnknown")
        println("Ambiguity: $markAmbiguity")


        val code = Language().getDictionaryCode(origLanguage, destLanguage)
        val mode = Language().getModeCode(origLanguage, destLanguage)
        val copiedDictionary = File(view.context.cacheDir, code).exists()
        if (!copiedDictionary) {
            return "<b>Not available, go to Dictionaries section and copy appropriate dictionary</b>"
        }
        if(code == "un-un"){
            return "<b>Not available</b>"
        }
        val offline = OfflineServiceProvider(
                code = mode,
                dir = File(view.context.cacheDir.absolutePath, code)
        )


        val jar = File(offline.dir, "classes.dex")
        println("JAR:" + jar.absolutePath + "\n")
        println("Directori" + offline.dir + "\n")

        val safeCacheDir = File(File(view.context.cacheDir, "packages"), "safe")
        fun getSafeCacheDir(): File {
            safeCacheDir.mkdirs()
            return safeCacheDir
        }

        val classLoader: ClassLoader = DexClassLoader(
            jar.absolutePath,
            getSafeCacheDir().absolutePath,
            null,
            javaClass.classLoader
        )

        val internalCacheDir = File(File(view.context.cacheDir, "packages"), "cache")

        val txtResult = ApertiumTranslator(
            offline.code,
            offline.dir,
            internalCacheDir,
            classLoader
        ).translate(text, markUnknown, markAmbiguity)
        val htmlResult: String = format(txtResult, markUnknown)

        println(htmlResult)
        return htmlResult
    }

    /*** Mitzuli (c) 2014 - 2016 Mikel Artetxe ***
     *** (c) 2023 - 2024 Joan Jerez            ***/

    private val unknownPattern = Pattern.compile("\\B\\*((\\p{L}||\\p{N})+)\\b")
    private fun escape(s: String?): String {
        return TextUtils.htmlEncode(s).replace("\n".toRegex(), "<br/>")
    }

    private fun format(s: String, markUnknown: Boolean): String {
        val htmlOutput = true
        val matcher: Matcher = unknownPattern.matcher(s)
        val sb = StringBuilder()

        if (htmlOutput) sb.append("<html>")
        var prevEnd = 0
        while (matcher.find()) {
            if (htmlOutput) sb.append(escape(s.substring(prevEnd, matcher.start()))) else sb.append(s.substring(prevEnd, matcher.start()))
            if (markUnknown) sb.append(if (htmlOutput) "<font color='#EE0000'>" else "*")
            if (htmlOutput) sb.append(escape(matcher.group(1))) else sb.append(matcher.group(1))
            if (markUnknown && htmlOutput) sb.append("</font>")
            prevEnd = matcher.end()
        }
        if (htmlOutput) sb.append(escape(s.substring(prevEnd))) else sb.append(s.substring(prevEnd))
        if (htmlOutput) sb.append("</html>")
        return sb.toString()
    }

    /*** end ***/

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)
        println("Origin Language: $item")
        val context = requireView().context
        // Create an ArrayAdapter using the string array and a default spinner layout.
        val dataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item)
        val itemNames: Array<String> = context.resources.getStringArray(R.array.dest_languages_array)
        destLanguage.adapter = Language().getAdapterBasedOnOrig(dataAdapter, itemNames, origLanguage.selectedItem.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}