package edu.uoc.jjerezt.translateocr.runtime.ocr

import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File

class TesseractRecognition : Recognition{
    override fun recognize(imgTest: File, dataPath: String, language: String): String {
        val tess = TessBaseAPI()
        if (!tess.init(dataPath, language)){
            tess.recycle()
        }
        tess.setImage(imgTest);
        val text2 = tess.utF8Text
        tess.recycle()
        return text2
    }


}