package com.example.project_shelf.framework.room

import android.content.Context
import androidx.room.Room

const val DATABASE_NAME = "shelf"
const val VERSION = 1

// Companion Singleton Class to encapsulate the SQLite Database.
class ShelfDatabase private constructor(val database: SqliteDatabase) {
    companion object {
        @Volatile
        private var instance: ShelfDatabase? = null

        fun getInstance(context: Context): ShelfDatabase {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ShelfDatabase(
                            Room.databaseBuilder(
                                context,
                                SqliteDatabase::class.java,
                                DATABASE_NAME
                            ).build()
                        )
                    }
                }
            }
            return instance!!
        }
    }
}