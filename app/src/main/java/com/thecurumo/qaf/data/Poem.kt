package com.thecurumo.qaf.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "poem",
    indices = [
        Index(value = ["id", "cat_id"], unique = true, name = "idx_poem_catid"),
        Index(value = ["id", "title"], unique = false, name = "idx_poem_title")
    ]
)
data class Poem(
    @PrimaryKey
    val id: Int?,
    val cat_id: Int?,
    val title: String?,
    val url: String?
)