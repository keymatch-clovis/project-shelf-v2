package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.DEFAULT_PAGE_SIZE
import com.example.project_shelf.adapter.dto.room.InvoiceDto
import com.example.project_shelf.adapter.dto.room.InvoiceFtsDto
import com.example.project_shelf.adapter.dto.room.InvoiceProductDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceFilter
import com.example.project_shelf.app.entity.InvoicePopulated
import com.example.project_shelf.app.entity.InvoiceWithCustomer
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

class InvoiceServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
) : InvoiceService {
    override fun get(): Flow<PagingData<Invoice>> {
        Log.d("SERVICE-IMPL", "Getting invoices")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) { database.invoiceDao().select() }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override fun getWithCustomer(): Flow<PagingData<InvoiceWithCustomer>> {
        Log.d("SERVICE-IMPL", "Getting invoices with customer")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) { database.invoiceDao().selectWithCustomer() }.flow.map {
            it.map { dto ->
                InvoiceWithCustomer(
                    invoice = dto.invoice.toEntity(),
                    customer = dto.customer.toEntity(),
                )
            }
        }
    }

    override fun getPopulated(): Flow<PagingData<InvoicePopulated>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentNumber(): Long {
        Log.d("SERVICE-IMPL", "Getting current invoice number")
        return database.invoiceDao().getMaxNumber()
    }

    override fun search(value: String): Flow<PagingData<InvoiceFilter>> {
        Log.d("SERVICE-IMPL", "Searching invoices with: $value")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.invoiceFtsDao().match("$value*")
        }.flow.map { it.map { dto -> dto.toEntity() } }
    }

    override suspend fun create(
        number: Long,
        customerId: Long,
        date: Date,
        products: List<InvoiceService.ProductParam>,
        discount: BigDecimal?
    ): Long {
        Log.d(
            "SERVICE-IMPL",
            "Creating invoice with: $number, $customerId, $date, $products, $discount"
        )
        return database.withTransaction {
            // Create the invoice
            val invoiceId = database.invoiceDao().insert(
                InvoiceDto(
                    number = number,
                    customerId = customerId,
                    date = Date().time,
                    discount = discount?.toLong(),
                )
            )

            // Create the invoice products.
            database.invoiceProductDao().insert(*products.map {
                InvoiceProductDto(
                    invoiceId = invoiceId,
                    productId = it.id,
                    count = it.count,
                    price = it.price.toString(),
                    discount = it.discount.toString(),
                )
            }.toTypedArray())

            // Get the customer to assign the search values.
            val customer = database.customerDao().select(customerId)

            // Finally, store the FTS value.
            database.invoiceFtsDao().insert(
                InvoiceFtsDto(
                    invoiceId = invoiceId,
                    number = number,
                    customerName = customer.name,
                    customerBusinessName = customer.businessName,
                )
            )

            invoiceId
        }
    }

    override suspend fun update() {
        TODO("Not yet implemented")
    }

    override suspend fun delete() {
        Log.d("SERVICE-IMPL", "Deleting all invoices")
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Deleting all invoice products")
            database.invoiceProductDao().delete()

            Log.d("SERVICE-IMPL", "Deleting all invoices")
            database.invoiceDao().delete()

            Log.d("SERVICE-IMPL", "Deleting all invoices FTS")
            database.invoiceFtsDao().delete()
        }
    }

    override suspend fun delete(id: Long) {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Invoice[$id]: Deleting invoice products")
            database.invoiceProductDao().deleteProducts(id)

            Log.d("SERVICE-IMPL", "Invoice[$id]: Deleting invoice")
            database.invoiceDao().delete(id)

            Log.d("SERVICE-IMPL", "Invoice[$id]: Deleting invoice FTS")
            database.invoiceFtsDao().delete(id)
        }
    }

    override suspend fun deletePendingForDeletion() {
        TODO("Not yet implemented")
    }

    override suspend fun setPendingForDeletion(id: Long, until: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun unsetPendingForDeletion(id: Long) {
        TODO("Not yet implemented")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class InvoiceModule {
    @Binds
    abstract fun bindService(impl: InvoiceServiceImpl): InvoiceService
}