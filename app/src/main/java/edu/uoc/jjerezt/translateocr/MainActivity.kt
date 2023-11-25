package edu.uoc.jjerezt.translateocr
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.uoc.jjerezt.translateocr.databinding.ActivityMainBinding

/*
* 2023-2024 Joan Jerez Torres
* GPL 3.0
* */

class MainActivity : AppCompatActivity() {

    /* fun <TranslationCallback, ExceptionCallback> translate(
        text: String?,
        markUnknown: Boolean,
        translationCallback: TranslationCallback,
        exceptionCallback: ExceptionCallback?)
    {
        TranslationTask(
            translationCallback,
            exceptionCallback,
            markUnknown
        ).execute(text);
    }

    fun translate(
        text: String?,
        translationCallback: com.mitzuli.core.mt.MtPackage.TranslationCallback?,
        exceptionCallback: ExceptionCallback?,
        markUnknown: Boolean,
        htmlOutput: Boolean
    ) {
        markUsage()
        com.mitzuli.core.mt.MtPackage.TranslationTask(
            translationCallback,
            exceptionCallback,
            markUnknown,
            htmlOutput
        ).execute(text)
    } */

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // https://developer.android.com/develop/ui/views/launch/splash-screen/migrate
        // https://developer.android.com/reference/kotlin/androidx/core/splashscreen/SplashScreen
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
        button.setOnClickListener {
            // Do something in response to button click
            println("Button pressed")
            var orig_language = "en";
            var dest_language = "ca";

        }


    }
}