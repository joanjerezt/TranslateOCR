package edu.uoc.jjerezt.translateocr.runtime.ocr

import android.content.Context
import edu.uoc.jjerezt.translateocr.runtime.Asset
import java.io.File

class Training {

    fun getCode(language: String): String {
        return when (language) {
            "Catalan" -> {
                "cat"
            }
            "English" -> {
                "eng"
            }
            "French" -> {
                "fra"
            }
            "Galician" -> {
                "glg"
            }
            "Portuguese" -> {
                "por"
            }
            "Spanish" -> {
                "spa"
            }
            else -> "eng"
        }
    }

    // https://developer.android.com/training/data-storage/app-specific#kotlin
    // https://stackoverflow.com/questions/11324348/why-app-appears-while-creating-directory-using-contextwraper-getdir
    fun copyLanguage(language: String, context: Context): String {
        val dir = context.getDir("tesseract", Context.MODE_PRIVATE)
        val createDir = File("$dir/tessdata")
        if (!createDir.exists()) {
            createDir.mkdir()
        }
        val trained = Asset().copyAssetToStorage(context, createDir, "$language.traineddata")
        println(trained.absolutePath)
        return File(context.filesDir.parent, "app_tesseract").absolutePath
    }
}