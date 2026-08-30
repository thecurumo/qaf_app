package com.thecurumo.qaf.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PoemDao {

    @Query("SELECT * FROM poem WHERE cat_id = :catId")
    suspend fun getPoemsByCategory(catId: Int): List<Poem>

    @Query("SELECT * FROM poem WHERE id = :poemId")
    suspend fun getPoemById(poemId: Int): Poem?

    @Query("SELECT * FROM poem WHERE title LIKE '%' || :query || '%'")
    suspend fun searchPoems(query: String): List<Poem>
}