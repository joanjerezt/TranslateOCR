package edu.uoc.jjerezt.translateocr.runtime.ocr

import java.io.File

interface Recognition {

    @Throws(Exception::class)
    fun recognize(imgTest: File, dataPath: String, language: String): String

}