package com.thecurumo.qaf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poet")
data class Poet(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val cat_id: Int?,
    val description: String?,
    val century_id: Int?,
    val birth_year: Int?,
    val death_year: Int?,
    val birthplace: String?,
    val deathplace: String?
)