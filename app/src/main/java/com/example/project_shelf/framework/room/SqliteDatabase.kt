package com.example.project_shelf.framework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project_shelf.adapter.dao.CustomerDao
import com.example.project_shelf.adapter.dao.ProductDao
import com.example.project_shelf.adapter.dto.CustomerDto
import com.example.project_shelf.adapter.dto.ProductDto

@Database(entities = [CustomerDto::class, ProductDto::class], version = VERSION)
abstract class SqliteDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
}