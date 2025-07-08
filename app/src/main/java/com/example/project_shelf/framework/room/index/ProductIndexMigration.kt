package com.example.project_shelf.framework.room.index

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class ProductIndex : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        // We need to create a partial index on the name column, as we are using soft deletes on
        // products. This makes the creation and editing way easier for us.
        db.execSQL(
            """
            CREATE UNIQUE INDEX idx_unique_name_not_pending_for_deletion
            ON product(name)
            WHERE pending_delete_until IS NULL
        """.trimIndent()
        )
    }
}