package edu.uoc.jjerezt.translateocr.runtime.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(private val entryRepository: EntryRepository): ViewModel() {

    fun insert(db: AppDatabase, entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.addEntry(db, entry)
        }
    }

    fun getAll(db: AppDatabase): List<Entry> {
        var entries : List<Entry> = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            entries = entryRepository.getEntries(db)
        }
        return entries
    }

    fun close(database: AppDatabase){
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.closeDatabase(database)
        }
    }

    fun edit(db: AppDatabase, entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.editEntry(db, entry)
        }
    }
}