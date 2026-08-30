package com.thecurumo.qaf.data

import android.content.Context
import androidx.room.Room
import java.io.File
import java.io.FileOutputStream

object DatabaseProvider {

    private const val DATABASE_NAME = "qaf.s3db"
    private const val ASSET_FILE_NAME = "qaf.s3db"

    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val dbFile = context.getDatabasePath(DATABASE_NAME)

            if (!dbFile.exists()) {
                dbFile.parentFile?.mkdirs()
                copyDatabase(context, dbFile)
            }

            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()

            instance = newInstance
            newInstance
        }
    }

    private fun copyDatabase(context: Context, targetFile: File) {
        context.assets.open(ASSET_FILE_NAME).use { assetStream ->
            FileOutputStream(targetFile).use { outputStream ->
                assetStream.copyTo(outputStream)
            }
        }
    }
}