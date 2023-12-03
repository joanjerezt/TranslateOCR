package edu.uoc.jjerezt.translateocr
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.googlecode.tesseract.android.TessBaseAPI
import dalvik.system.DexClassLoader
import edu.uoc.jjerezt.translateocr.databinding.ActivityMainBinding
import edu.uoc.jjerezt.translateocr.runtime.Asset
import edu.uoc.jjerezt.translateocr.runtime.OfflineServiceProvider
import edu.uoc.jjerezt.translateocr.runtime.text.ApertiumTranslator
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
* 2023-2024 Joan Jerez Torres
* GPL 3.0
* */

class MainActivity : AppCompatActivity() {

    private val unknownPattern = Pattern.compile("\\B\\*((\\p{L}||\\p{N})+)\\b")

    private fun escape(s: String?, htmlOutput: Boolean): String? {
        return if (htmlOutput) TextUtils.htmlEncode(s).replace("\n".toRegex(), "<br/>") else s
    }

    private fun format(s: String, htmlOutput: Boolean, markUnknown: Boolean): String {
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // https://developer.android.com/develop/ui/views/launch/splash-screen/migrate
        // https://developer.android.com/reference/kotlin/androidx/core/splashscreen/SplashScreen
        // https://commons.wikimedia.org/wiki/File:Oxygen480-categories-applications-development-translation.svg
        val screen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        val viewModel = object { val isReady = true }
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    return if (viewModel.isReady) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            }
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dictionaries, R.id.navigation_history, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val button: Button = findViewById(R.id.button)
        var text : EditText = findViewById(R.id.editTextTextMultiLine)
        button.setOnClickListener {
            // Do something in response to button click
            print("Button pressed")
            val orig_language : TextView = findViewById(R.id.textView);
            val dest_language : TextView = findViewById(R.id.textView2);
            text = findViewById(R.id.editTextTextMultiLine)
            val text_sortida = translate(orig_language.text.toString(), dest_language.text.toString(), text.text.toString())
            val text2: EditText = findViewById(R.id.editTextTextMultiLine2)
            text2.setText(text_sortida);
        }
        // https://developer.android.com/training/data-storage/app-specific#kotlin
        // https://stackoverflow.com/questions/11324348/why-app-appears-while-creating-directory-using-contextwraper-getdir
        val tess = TessBaseAPI()
        val img: Button = findViewById(R.id.button2)
        img.setOnClickListener {
            val imgTest = Asset().copyAssetToCache(this, "hello_world.png")
            val dir = this.getDir("tesseract", Context.MODE_PRIVATE)
            val createDir = File("$dir/tessdata")
            if (!createDir.exists()) {
                createDir.mkdir()
            }
            val trained = Asset().copyAssetToStorage(this, createDir,"eng.traineddata")
            println(trained.absolutePath)
            val dataPath: String = File(this.filesDir.parent, "app_tesseract").absolutePath
            println(dataPath)
            if (!tess.init(dataPath, "eng")){
                tess.recycle()
                return@setOnClickListener;
            }
            tess.setImage(imgTest);
            val text2 = tess.utF8Text
            println(text2)
            text.setText(text2)
        }
    }




    private fun translate(origLanguage: String, destLanguage: String, text: String): String {

        val file = Asset().copyAssetToCache(this, "apertium-en-ca.jar")

        /* try{
            Asset().extractJarFile(file)
        }
        catch(e: Exception){
            print(e.localizedMessage)
        } */

        val htmlOutput = false;
        val markUnknown = true;
        val offline = OfflineServiceProvider(
            code = "ca-en",
            dir =  file.parentFile!!,
        )

        val jar = File(offline.dir, "classes.dex")
        print("JAR:" + jar.absolutePath + "\n")
        print("Directori" + offline.dir + "\n")

        val safeCacheDir = File(File(this.cacheDir, "packages"), "safe")
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

        val internalCacheDir = File(File(this.cacheDir, "packages"), "cache")
        var htmlResult = ""

        htmlResult = format(
                ApertiumTranslator(
                    offline.code,
                    offline.dir,
                    internalCacheDir,
                    classLoader
                ).translate(text), htmlOutput, markUnknown
            )

        print(htmlResult)

        return htmlResult
    }
}