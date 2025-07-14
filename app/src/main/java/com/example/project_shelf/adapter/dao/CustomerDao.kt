package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.CustomerDto
import com.example.project_shelf.adapter.dto.room.CustomerFtsDto

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer WHERE pending_delete_until IS NULL")
    fun select(): PagingSource<Int, CustomerDto>

    @Query("SELECT * FROM customer WHERE pending_delete_until IS NOT NULL")
    suspend fun selectPendingForDeletion(): List<CustomerDto>

    @Insert
    suspend fun insert(dto: CustomerDto): Long

    @Update
    suspend fun update(dto: CustomerDto)

    @Query("DELETE FROM customer")
    suspend fun delete()

    @Query("DELETE FROM customer WHERE rowid = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE customer SET pending_delete_until = :until WHERE rowid = :id")
    suspend fun setPendingForDeletion(id: Long, until: Long)

    @Query("UPDATE customer SET pending_delete_until = NULL WHERE rowid = :id")
    suspend fun unsetPendingForDeletion(id: Long)
}

@Dao
interface CustomerFtsDao {
    @Insert
    suspend fun insert(dto: CustomerFtsDto)

    @Query("DELETE FROM customer_fts")
    suspend fun delete()

    @Query("DELETE FROM customer_fts WHERE customer_id = :customerId")
    suspend fun delete(customerId: Long)

    @Query(
        """
        SELECT e.* FROM customer_fts fts
        JOIN customer e ON (e.rowid = fts.customer_id)
        WHERE customer_fts MATCH :value
    """
    )
    fun match(value: String): PagingSource<Int, CustomerDto>
}