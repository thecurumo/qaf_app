package com.thecurumo.qaf.data

import androidx.room.Entity

@Entity(tableName = "verse", primaryKeys = ["poem_id", "vorder"])
data class Verse(
    val poem_id: Int,
    val vorder: Int,
    val position: Int,
    val text: String
)