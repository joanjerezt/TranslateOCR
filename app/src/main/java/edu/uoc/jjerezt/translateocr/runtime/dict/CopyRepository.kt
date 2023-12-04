package edu.uoc.jjerezt.translateocr.runtime.dict

import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.runtime.Asset
import java.io.File

class CopyRepository {

        fun removeDictionary(dictionary: TextView, holder: ViewHolder, download: Button) {
                val code: String = Language().getCode(dictionary)
                val context = holder.itemView.context
                val subdir = File(context.cacheDir.absolutePath, code)
                val deleted: Boolean = subdir.delete()
                if(deleted){
                        download.setText(R.string.download)
                        val color = context.resources.getColor(R.color.green, context.theme)
                        download.setBackgroundColor(color)
                }

        }
        fun selectDictionary(dictionary: TextView, holder: RecyclerView.ViewHolder, download: Button) {
                val code: String = Language().getCode(dictionary)
                copyDictionary(code, holder)
                download.setText(R.string.cache)
                val context = holder.itemView.context
                val red = context.resources.getColor(R.color.red, context.theme)
                download.setBackgroundColor(red)
        }



        private fun copyDictionary(code: String, holder: RecyclerView.ViewHolder){
                val file = Asset().copyAssetToCache(holder.itemView.context, "apertium-$code.jar")
                val subdir = File(file.parentFile?.absolutePath, code)
                try{
                        Asset().extractJarFile(file, subdir)
                } catch(e: Exception){
                        print(e.localizedMessage)
                }
        }

}