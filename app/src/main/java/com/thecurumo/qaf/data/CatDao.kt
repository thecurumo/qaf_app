package com.thecurumo.qaf.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CatDao {

    @Query("SELECT * FROM cat WHERE poet_id = :poetId AND parent_id = 0 LIMIT 1")
    suspend fun getRootCategory(poetId: Int): Cat?

    @Query("SELECT * FROM cat WHERE parent_id = :parentId")
    suspend fun getChildCategories(parentId: Int): List<Cat>

    @Query("SELECT * FROM cat WHERE id = :catId")
    suspend fun getCategoryById(catId: Int): Cat?
}