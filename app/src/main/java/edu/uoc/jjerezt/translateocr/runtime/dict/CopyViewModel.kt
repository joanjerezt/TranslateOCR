package edu.uoc.jjerezt.translateocr.runtime.dict
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CopyViewModel(private val copyRepository: CopyRepository): ViewModel() {
    // https://developer.android.com/kotlin/coroutines
    fun copy(content: TextView, holder: RecyclerView.ViewHolder, download: Button) {
        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            copyRepository.selectDictionary(content, holder, download)
        }
    }
    fun delete(content: TextView, holder: RecyclerView.ViewHolder, download: Button){
        viewModelScope.launch(Dispatchers.IO) {
            copyRepository.removeDictionary(content, holder, download)
        }
    }



}
