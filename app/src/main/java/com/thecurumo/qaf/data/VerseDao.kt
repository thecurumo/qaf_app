package com.thecurumo.qaf.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface VerseDao {

    @Query("SELECT * FROM verse WHERE poem_id = :poemId ORDER BY vorder ASC")
    suspend fun getVersesForPoem(poemId: Int): List<Verse>

    @Query("SELECT * FROM verse WHERE poem_id = :poemId AND vorder IN (:orders) ORDER BY vorder ASC")
    suspend fun getOpeningVerses(poemId: Int, orders: List<Int>): List<Verse>
}