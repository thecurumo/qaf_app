package com.thecurumo.qaf.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PoetDao {

    @Query("SELECT * FROM poet ORDER BY birth_year ASC")
    suspend fun getAllPoets(): List<Poet>

    @Query("SELECT * FROM poet WHERE id = :poetId")
    suspend fun getPoetById(poetId: Int): Poet?

    @Query("SELECT * FROM poet WHERE name LIKE '%' || :query || '%'")
    suspend fun searchPoets(query: String): List<Poet>
}