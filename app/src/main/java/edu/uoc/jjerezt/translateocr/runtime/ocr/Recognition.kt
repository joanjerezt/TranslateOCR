package edu.uoc.jjerezt.translateocr.runtime.ocr

interface Recognition {

    @Throws(Exception::class)
    fun recognize(text: String?): String?

}