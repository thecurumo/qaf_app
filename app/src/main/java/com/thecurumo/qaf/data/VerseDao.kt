package com.thecurumo.qaf.data

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface VerseDao {

    @RawQuery
    suspend fun getVersesForPoemRaw(query: SupportSQLiteQuery): List<VerseRaw>

    suspend fun getVersesForPoem(poemId: Int): List<VerseRaw> {
        val query = androidx.sqlite.db.SimpleSQLiteQuery(
            "SELECT poem_id, vorder, position, text FROM verse WHERE poem_id = ? ORDER BY vorder ASC",
            arrayOf(poemId)
        )
        return getVersesForPoemRaw(query)
    }
}