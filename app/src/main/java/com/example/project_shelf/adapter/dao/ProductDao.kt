package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto

@Dao
interface ProductDao {
    @Query("SELECT * FROM product WHERE pending_delete_until IS NULL")
    fun select(): PagingSource<Int, ProductDto>

    // NOTE:
    //  As we are creating a partial index with the product name, this query MUST always return a
    //  single item.
    @Query("SELECT * FROM product WHERE name = :name AND pending_delete_until IS NULL")
    suspend fun select(name: String): ProductDto

    @Query("SELECT * FROM product WHERE pending_delete_until IS NOT NULL")
    suspend fun selectPendingForDeletion(): List<ProductDto>

    @Query("SELECT * FROM product WHERE name = :name AND pending_delete_until IS NULL")
    suspend fun selectByName(name: String): ProductDto?

    @Insert
    suspend fun insert(dto: ProductDto): Long

    @Update
    suspend fun update(dto: ProductDto)

    @Query("DELETE FROM product")
    suspend fun delete()

    @Query("DELETE FROM product WHERE rowid = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE product SET pending_delete_until = :until WHERE rowid = :id")
    suspend fun setPendingForDeletion(id: Long, until: Long)

    @Query("UPDATE product SET pending_delete_until = NULL WHERE rowid = :id")
    suspend fun unsetPendingForDeletion(id: Long)
}

@Dao
interface ProductFtsDao {
    @Insert
    suspend fun insert(dto: ProductFtsDto)

    @Query("DELETE FROM product_fts")
    suspend fun delete()

    @Query("DELETE FROM product_fts WHERE product_id = :productId")
    suspend fun delete(productId: Long)

    // As we support soft deletion, we have to make sure the products returned by the FTS are not
    // marked for deletion.
    @Query(
        """
       SELECT e.* FROM product_fts fts
       JOIN product e ON (e.rowid = fts.product_id)
       WHERE
        e.pending_delete_until IS NULL
        AND product_fts MATCH :value 
    """
    )
    fun match(value: String): PagingSource<Int, ProductDto>
}