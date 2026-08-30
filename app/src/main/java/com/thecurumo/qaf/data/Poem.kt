package com.thecurumo.qaf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poem")
data class Poem(
    @PrimaryKey
    val id: Int,
    val cat_id: Int,
    val title: String,
    val url: String?
)