package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.CustomerDto
import com.example.project_shelf.adapter.dto.room.CustomerFtsDto

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer")
    fun select(): PagingSource<Int, CustomerDto>

    @Query("DELETE FROM product")
    suspend fun delete()

    @Delete
    suspend fun delete(dto: CustomerDto)

    @Insert
    suspend fun insert(dto: CustomerDto): Long

    @Update
    suspend fun update(dto: CustomerDto)
}

@Dao
interface CustomerFtsDao {
    @Insert
    suspend fun insert(dto: CustomerFtsDto)

    @Query("SELECT * FROM customer_fts WHERE customer_fts MATCH :value")
    fun match(value: String): PagingSource<Int, CustomerFtsDto>
}