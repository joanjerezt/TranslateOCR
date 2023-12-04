package edu.uoc.jjerezt.translateocr.runtime

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarFile

class Asset {

    // https://stackoverflow.com/a/56455963
    @Throws(IOException::class)
    fun copyAssetToCache(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open("apertium/$fileName").use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }

    fun copyAssetToStorage(context: Context, dir: File, fileName: String): File = File(dir.absolutePath, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { storage ->
                    context.assets.open("tessdata/$fileName").use { inputStream ->
                        inputStream.copyTo(storage)
                    }
                }
            }
        }

    // https://stackoverflow.com/a/13084442
    fun extractJarFile(jar: File, destdir: File){
        val jarfile = JarFile(jar)
        val enu = jarfile.entries()
        while (enu.hasMoreElements()) {
            // val destdir = subdir //abc is my destination directory
            destdir.mkdir()
            println(destdir)
            val je = enu.nextElement()
            println(je.name)
            if(je.name != "data/apertium-en-ca.en-ca.genitive.t1x"){
                var fl = File(destdir, je.name)
                if (!fl.exists()) {
                    fl.parentFile?.mkdir()
                    fl = File(destdir, je.name)
                }
                if (je.isDirectory) {
                    continue
                }
                val inputStream = jarfile.getInputStream(je)
                val fo = FileOutputStream(fl)
                while (inputStream.available() > 0) {
                    fo.write(inputStream.read())
                }
                fo.close()
                inputStream.close()
            }

        }

    }
}

