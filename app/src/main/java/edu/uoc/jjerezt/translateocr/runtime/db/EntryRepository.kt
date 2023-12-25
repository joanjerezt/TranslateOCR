package edu.uoc.jjerezt.translateocr.runtime.db

class EntryRepository {

    fun addEntry(db: AppDatabase, entry: Entry) {
        db.entryDao().insertAll(entry)
    }

    fun getEntries(db: AppDatabase): List<Entry> {
        return db.entryDao().getAll()
    }

    fun closeDatabase(db: AppDatabase){
        db.close()
    }

    fun editEntry(db: AppDatabase, entry: Entry){
        db.entryDao().edit(entry)
    }
}