package edu.uoc.jjerezt.translateocr.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.uoc.jjerezt.translateocr.MainActivity
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentDictionaryBinding
import edu.uoc.jjerezt.translateocr.placeholder.PlaceholderContent.PlaceholderItem
import edu.uoc.jjerezt.translateocr.runtime.Asset

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyDictionaryRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyDictionaryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentDictionaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        // holder.idView.text = item.id
        holder.contentView.text = item.content

        val download = holder.downloadButton
        download.setOnClickListener {
            when (download.text) {
                "Copying" -> {
                    val mySnackbar = Snackbar.make(holder.itemView, "Please wait...", 5000)
                    mySnackbar.show()
                }
                "Delete" -> {
                    // clear folder
                    println("Clearing folder")
                    download.setText(R.string.download)
                }
                "Available" -> {
                    println("Download button pressed")
                    // copy dictionary to cache
                    val content2: TextView = holder.contentView
                    println(content2.text)
                    download.setText(R.string.copying)
                    if(content2.text.equals("English - Catalan")){
                        val file = Asset().copyAssetToCache(MainActivity(), "apertium-en-ca.jar")
                        try{
                            Asset().extractJarFile(file)
                        } catch(e: Exception){
                            print(e.localizedMessage)
                        }
                    }
                    download.setText(R.string.cache)
                }
            }


        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.contentDict
        val downloadButton: Button = binding.download

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}