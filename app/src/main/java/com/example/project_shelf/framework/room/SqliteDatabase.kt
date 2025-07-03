package com.example.project_shelf.framework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project_shelf.adapter.dao.CityDao
import com.example.project_shelf.adapter.dao.CityFtsDao
import com.example.project_shelf.adapter.dao.CustomerDao
import com.example.project_shelf.adapter.dao.CustomerFtsDao
import com.example.project_shelf.adapter.dao.InvoiceDao
import com.example.project_shelf.adapter.dao.InvoiceFtsDao
import com.example.project_shelf.adapter.dao.InvoiceProductDao
import com.example.project_shelf.adapter.dao.ProductDao
import com.example.project_shelf.adapter.dao.ProductFtsDao
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.adapter.dto.room.CityFtsDto
import com.example.project_shelf.adapter.dto.room.CustomerDto
import com.example.project_shelf.adapter.dto.room.CustomerFtsDto
import com.example.project_shelf.adapter.dto.room.InvoiceDto
import com.example.project_shelf.adapter.dto.room.InvoiceFtsDto
import com.example.project_shelf.adapter.dto.room.InvoiceProductDto
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto

@Database(
    entities = [
        CustomerDto::class,
        CustomerFtsDto::class,
        ProductDto::class,
        ProductFtsDto::class,
        CityDto::class,
        CityFtsDto::class,
        InvoiceDto::class,
        InvoiceProductDto::class,
        InvoiceFtsDto::class,
    ],
    version = VERSION,
)
abstract class SqliteDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun customerFtsDao(): CustomerFtsDao
    abstract fun productDao(): ProductDao
    abstract fun productFtsDao(): ProductFtsDao
    abstract fun cityDao(): CityDao
    abstract fun cityFtsDao(): CityFtsDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceProductDao(): InvoiceProductDao
    abstract fun invoiceFtsDao(): InvoiceFtsDao
}