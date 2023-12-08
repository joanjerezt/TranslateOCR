package edu.uoc.jjerezt.translateocr

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import dalvik.system.DexClassLoader
import edu.uoc.jjerezt.translateocr.runtime.Asset
import edu.uoc.jjerezt.translateocr.runtime.OfflineServiceProvider
import edu.uoc.jjerezt.translateocr.runtime.ocr.TesseractRecognition
import edu.uoc.jjerezt.translateocr.runtime.ocr.Training
import edu.uoc.jjerezt.translateocr.runtime.text.ApertiumTranslator

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    /**
     * Es prova que la traducció de l'anglès al català, a partir del text anglès "Hello!", retorna el resultat esperat
     */

    @Test
    fun textTranslation(){

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val mode = "en-ca"
        val code = "en-ca"
        val text = "Hello!"
        val markUnknown = true
        val markAmbiguity = false

        val file = Asset().copyDictionaryToCache(appContext, "apertium-$code.jar")
        val subdir = File(file.parentFile?.absolutePath, code)
        try{
            Asset().extractJarFile(file, subdir)
        } catch(e: Exception){
            print(e.localizedMessage)
        }

        val offline = OfflineServiceProvider(
            code = mode,
            dir = File(appContext.cacheDir.absolutePath, code)
        )
        val jar = File(offline.dir, "classes.dex")
        val safeCacheDir = File(File(appContext.cacheDir, "packages"), "safe")
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

        val internalCacheDir = File(File(appContext.cacheDir, "packages"), "cache")

        val txtResult = ApertiumTranslator(
            offline.code,
            offline.dir,
            internalCacheDir,
            classLoader
        ).translate(text, markUnknown, markAmbiguity)
        assertEquals("Hola!", txtResult.trim())
    }

    /**
     * Es prova que la traducció de l'anglès al català, a partir de la imatge de prova, retorna el resultat esperat
     * S'ha fet servir el reconeixement òptic per obtenir el text "Hello World!"
     */

    @Test
    fun ocrTranslation(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val tessLanguage = "eng"
        val mode = "en-ca"
        val code = "en-ca"
        val mediaFile = Asset().copyAssetToCache(appContext, "hello_world.png")
        val dataPath = Training().copyLanguage("eng", appContext)
        val text = TesseractRecognition().recognize(mediaFile, dataPath, tessLanguage)
        val markUnknown = true
        val markAmbiguity = false

        val file = Asset().copyDictionaryToCache(appContext, "apertium-$code.jar")
        val subdir = File(file.parentFile?.absolutePath, code)
        try{
            Asset().extractJarFile(file, subdir)
        } catch(e: Exception){
            print(e.localizedMessage)
        }

        val offline = OfflineServiceProvider(
            code = mode,
            dir = File(appContext.cacheDir.absolutePath, code)
        )
        val jar = File(offline.dir, "classes.dex")
        val safeCacheDir = File(File(appContext.cacheDir, "packages"), "safe")
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

        val internalCacheDir = File(File(appContext.cacheDir, "packages"), "cache")

        val txtResult = ApertiumTranslator(
            offline.code,
            offline.dir,
            internalCacheDir,
            classLoader
        ).translate(text, markUnknown, markAmbiguity)
        assertEquals("Món\nd'hola!", txtResult)
    }
}