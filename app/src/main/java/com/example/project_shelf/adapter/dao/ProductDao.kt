package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto

@Dao
interface ProductDao {
    @Query("SELECT * FROM product WHERE pending_delete_until IS NULL")
    fun select(): PagingSource<Int, ProductDto>

    @Query("SELECT * FROM PRODUCT WHERE name = :name AND pending_delete_until IS NULL")
    suspend fun select(name: String): ProductDto?

    @Query("DELETE FROM product")
    suspend fun delete()

    @Query("DELETE FROM product WHERE rowid = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM product WHERE pending_delete_until IS NOT NULL")
    suspend fun deletePendingForDeletion()

    @Query("UPDATE product SET pending_delete_until = :until WHERE rowid = :id")
    suspend fun markForDeletion(id: Long, until: Long)

    @Query("UPDATE product SET pending_delete_until = NULL WHERE rowid = :id")
    suspend fun unmarkForDeletion(id: Long)

    @Insert
    suspend fun insert(dto: ProductDto): Long

    @Update
    suspend fun update(dto: ProductDto)
}

@Dao
interface ProductFtsDao {
    @Insert
    suspend fun insert(dto: ProductFtsDto)

    @Query("DELETE FROM product_fts WHERE product_id = :productId")
    suspend fun delete(productId: Long)

    @Query("SELECT * FROM product_fts WHERE product_fts MATCH :value")
    fun match(value: String): PagingSource<Int, ProductFtsDto>
}