package edu.uoc.jjerezt.translateocr.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentDictionaryBinding
import edu.uoc.jjerezt.translateocr.placeholder.PlaceholderContent.PlaceholderItem
import edu.uoc.jjerezt.translateocr.runtime.DataStoreManager
import edu.uoc.jjerezt.translateocr.runtime.dict.CopyRepository
import edu.uoc.jjerezt.translateocr.runtime.dict.CopyViewModel
import edu.uoc.jjerezt.translateocr.runtime.dict.Language
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

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
        val context = holder.itemView.context
        val defaultColor = context.resources.getColor(R.color.green, context.theme)
        val orange = context.resources.getColor(R.color.orange, context.theme)
        val red = context.resources.getColor(R.color.red, context.theme)
        var status = 0
        val code = Language().getCode(holder.contentView)
        DataStoreManager.dict[code] = intPreferencesKey("dict_${code}")
        runBlocking {
            withTimeoutOrNull(2000) {
                status = DataStoreManager().readValue( holder.itemView.context, DataStoreManager.dict[code]!!
                ) ?: 0
            }
        }
        when (status) {
            1 -> {
                download.text = "Copying"
                download.setBackgroundColor(orange)
            }
            0 -> {
                download.text = "Available"
                download.setBackgroundColor(defaultColor)
            }
            2 -> {
                download.text = "Delete"
                download.setBackgroundColor(red)
            }
        }
        download.setOnClickListener {
            when (download.text) {
                "Copying" -> {
                    val mySnackbar = Snackbar.make(holder.itemView, "Please wait...", 5000)
                    mySnackbar.show()
                }
                "Delete" -> {
                    // clear folder
                    println("Clearing folder")
                    val dictionary: TextView = holder.contentView
                    CopyViewModel(copyRepository = CopyRepository()).delete(dictionary, holder, download)

                }
                "Available" -> {
                    println("Download button pressed")
                    // copy dictionary to cache
                    val dictionary: TextView = holder.contentView
                    download.setText(R.string.copying)
                    download.setBackgroundColor(orange)
                    CopyViewModel(copyRepository = CopyRepository()).copy(dictionary, holder, download)

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