package edu.uoc.jjerezt.translateocr.runtime.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Entry(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "time") val timestamp: Date,
    @ColumnInfo(name = "orig_text") val origText: String,
    @ColumnInfo(name = "dest_text") val destText: String,
    @ColumnInfo(name = "dictionary") val dictionary: Dictionary,
    @ColumnInfo(name = "favorite") val favorite: Boolean
    )
