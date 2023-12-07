package edu.uoc.jjerezt.translateocr.runtime.dict
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.jjerezt.translateocr.runtime.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CopyViewModel(private val copyRepository: CopyRepository): ViewModel() {
    // https://developer.android.com/kotlin/coroutines

    fun copy(content: TextView, holder: RecyclerView.ViewHolder, download: Button) {
        val code = Language().getCode(content)
        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            DataStoreManager().saveValue(holder.itemView.context, DataStoreManager.dict[code]!!, 1)
            copyRepository.selectDictionary(content, holder, download)
            DataStoreManager().saveValue(holder.itemView.context, DataStoreManager.dict[code]!!, 2)
        }
    }
    fun delete(content: TextView, holder: RecyclerView.ViewHolder, download: Button){
        val code = Language().getCode(content)
        viewModelScope.launch(Dispatchers.IO) {
            copyRepository.removeDictionary(content, holder, download)
            DataStoreManager().saveValue(holder.itemView.context, DataStoreManager.dict[code]!!, 0)
        }
    }



}
