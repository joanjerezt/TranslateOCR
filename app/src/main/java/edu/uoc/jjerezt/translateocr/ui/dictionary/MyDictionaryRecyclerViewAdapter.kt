package edu.uoc.jjerezt.translateocr.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentDictionaryBinding
import edu.uoc.jjerezt.translateocr.placeholder.PlaceholderContent.PlaceholderItem
import edu.uoc.jjerezt.translateocr.runtime.dict.CopyRepository
import edu.uoc.jjerezt.translateocr.runtime.dict.CopyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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



    private fun checkCopiedDictionaries(){
        TODO()
    }

    // https://stackoverflow.com/questions/55208748/asynctask-as-kotlin-coroutine
    private fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO) { // runs in background thread without blocking the Main Thread
            doInBackground()
        }
        onPostExecute(result)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        // holder.idView.text = item.id
        holder.contentView.text = item.content
        val download = holder.downloadButton
        val context = holder.itemView.context
        val color = context.resources.getColor(R.color.green, context.theme)
        val orange = context.resources.getColor(R.color.orange, context.theme)
        download.setBackgroundColor(color)
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
                    println(dictionary.text)
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