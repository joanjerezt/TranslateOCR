package edu.uoc.jjerezt.translateocr.runtime.text

interface Translator {
    @Throws(Exception::class)
    fun translate(text: String?, displayMarks: Boolean, displayAmbiguity: Boolean): String?
}