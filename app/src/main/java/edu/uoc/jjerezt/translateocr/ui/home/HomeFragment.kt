package edu.uoc.jjerezt.translateocr.ui.home
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dalvik.system.DexClassLoader
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentHomeBinding
import edu.uoc.jjerezt.translateocr.runtime.OfflineServiceProvider
import edu.uoc.jjerezt.translateocr.runtime.dict.Language
import edu.uoc.jjerezt.translateocr.runtime.text.ApertiumTranslator
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        // val destLanguage : TextView = root.findViewById(R.id.textView2);
        destLanguage = root.findViewById(R.id.spinner2)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        val dataAdapter = ArrayAdapter<String>(root.context, android.R.layout.simple_spinner_dropdown_item)
        val itemNames: Array<String> = root.context.resources.getStringArray(R.array.dest_languages_array)
        destLanguage.adapter = Language().getAdapterBasedOnOrig(dataAdapter, itemNames, origLanguage)

        // https://stackoverflow.com/questions/71157667/how-to-change-default-input-language-in-textinputedittext-java-android
        // https://stackoverflow.com/questions/56387603/how-we-can-specify-input-language-for-specific-edittextnot-for-whole-app-any-h
        // origText.imeHintLocales = LocaleList(Locale("ar", "SA"))

        translateButton.setOnClickListener {
            println("Translate button pressed")
            val textSortida = translate(root.rootView, origLanguage.selectedItem.toString(), destLanguage.selectedItem.toString(), origText.text.toString())
            // https://stackoverflow.com/questions/2116162/how-to-display-html-in-textview
            val spanned = HtmlCompat.fromHtml(textSortida, HtmlCompat.FROM_HTML_MODE_COMPACT)
            destText.text = spanned;
        }

        return root
    }

    private lateinit var destLanguage: Spinner
    private lateinit var origLanguage: Spinner

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun translate(
        view: View,
        origLanguage: String,
        destLanguage: String,
        text: String
    ): String {

        val markUnknown = true
        val markAmbiguity = true

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
        var htmlResult = "<span></span>"

        htmlResult = format(
            ApertiumTranslator(
                offline.code,
                offline.dir,
                internalCacheDir,
                classLoader
            ).translate(text), markUnknown
        )

        println(htmlResult)
        return htmlResult
    }

    /*** Mitzuli                          ***
     *** (c) 2014 - 2016 Mikel Artetxe    ***/

    private val unknownPattern = Pattern.compile("\\B\\*((\\p{L}||\\p{N})+)\\b")
    private fun escape(s: String?, htmlOutput: Boolean): String? {
        return if (htmlOutput) TextUtils.htmlEncode(s).replace("\n".toRegex(), "<br/>") else s
    }

    private fun format(s: String, markUnknown: Boolean): String {
        val htmlOutput = true
        val matcher: Matcher = unknownPattern.matcher(s)
        val sb = StringBuilder()

        if (htmlOutput) sb.append("<html>")
        var prevEnd = 0
        while (matcher.find()) {
            sb.append(escape(s.substring(prevEnd, matcher.start()), htmlOutput))
            if (markUnknown) sb.append(if (htmlOutput) "<font color='#EE0000'>" else "*")
            sb.append(escape(matcher.group(1),htmlOutput))
            if (markUnknown && htmlOutput) sb.append("</font>")
            prevEnd = matcher.end()
        }
        sb.append(escape(s.substring(prevEnd), htmlOutput))
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
        destLanguage.adapter = Language().getAdapterBasedOnOrig(dataAdapter, itemNames, origLanguage)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}