package edu.uoc.jjerezt.translateocr

import edu.uoc.jjerezt.translateocr.runtime.dict.Language
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun getCodeDictionary(){
        val origLanguage = "Catalan"
        val destLanguage = "English"
        val code = Language().getDictionaryCode(origLanguage, destLanguage)
        assertEquals(code, "en-ca")
    }
}