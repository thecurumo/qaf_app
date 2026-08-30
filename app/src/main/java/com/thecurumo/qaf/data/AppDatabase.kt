package com.thecurumo.qaf.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Poet::class, Cat::class, Poem::class, Verse::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun poetDao(): PoetDao
    abstract fun catDao(): CatDao
    abstract fun poemDao(): PoemDao
    abstract fun verseDao(): VerseDao
}