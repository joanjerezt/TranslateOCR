package edu.uoc.jjerezt.translateocr.runtime.text

import java.io.File

class ApertiumTranslator(
    private val code: String,
    private val base: File,
    private val cacheDir: File,
    private val classLoader: ClassLoader
) : Translator {
    @Throws(Exception::class)
    override fun translate(text: String?): String {

        // https://wiki.apertium.org/wiki/User:Mikel/Embeddable_lttoolbox-java:_Progress

        synchronized(org.apertium.Translator::class.java) {
            org.apertium.Translator.setDisplayMarks(true)
            org.apertium.Translator.setBase(base.absolutePath, classLoader)
            org.apertium.Translator.setMode(code)
            org.apertium.utils.IOUtils.cacheDir = cacheDir
            return org.apertium.Translator.translate(text)
        }
    }
}
