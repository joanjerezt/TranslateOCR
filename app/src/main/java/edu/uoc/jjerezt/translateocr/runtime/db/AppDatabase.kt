package edu.uoc.jjerezt.translateocr.runtime.db

import androidx.room.Database
import androidx.room.RoomDatabase

// https://developer.android.com/training/data-storage/room

@Database(entities = [Entry::class, Dictionary::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun dictionaryDao(): DictionaryDao
}
