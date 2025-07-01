package com.example.project_shelf.framework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project_shelf.adapter.dao.CityDao
import com.example.project_shelf.adapter.dao.CustomerDao
import com.example.project_shelf.adapter.dao.ProductDao
import com.example.project_shelf.adapter.dao.ProductFtsDao
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.adapter.dto.room.CustomerDto
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto

@Database(
    entities = [CustomerDto::class, ProductDto::class, ProductFtsDto::class, CityDto::class],
    version = VERSION
)
abstract class SqliteDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun productFtsDao(): ProductFtsDao
    abstract fun cityDao(): CityDao
}