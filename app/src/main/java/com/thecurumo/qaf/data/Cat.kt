package com.thecurumo.qaf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat")
data class Cat(
    @PrimaryKey
    val id: Int,
    val poet_id: Int?,
    val text: String?,
    val parent_id: Int?,
    val url: String?
)