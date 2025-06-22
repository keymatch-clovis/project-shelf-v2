package com.example.project_shelf.adapter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_shelf.adapter.dto.room.CustomerDto

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer")
    fun select(): List<CustomerDto>

    @Insert
    suspend fun insert(dto: CustomerDto)
}