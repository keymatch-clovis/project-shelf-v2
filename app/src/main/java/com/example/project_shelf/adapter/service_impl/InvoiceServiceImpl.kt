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
import com.example.project_shelf.adapter.dto.room.toDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceFilter
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigInteger
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

    override fun search(value: String): Flow<PagingData<Invoice>> {
        Log.d("SERVICE-IMPL", "Searching invoices with: $value")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.invoiceFtsDao().match("$value*")
        }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override suspend fun create(customer: Customer, discount: BigInteger) {
        Log.d("SERVICE-IMPL", "Creating invoice with: $customer, $discount")
        database.withTransaction {
            val dao = database.invoiceDao()

            // First, get the consecutive number from the database.
            val consecutive = dao.getCurrentInvoiceNumber() + 1

            // Create the invoice
            val invoiceId = dao.insert(
                InvoiceDto(
                    number = consecutive,
                    date = Date().time,
                    discount = discount.toLong(),
                    customerId = customer.id,
                )
            )

            // Finally, store the FTS value.
            database.invoiceFtsDao().insert(
                InvoiceFtsDto(
                    invoiceId = invoiceId,
                    number = consecutive,
                    customerName = customer.name,
                    customerBusinessName = customer.businessName,
                )
            )
        }
    }

    override suspend fun delete(invoice: Invoice) {
        Log.d("SERVICE-IMPL", "Deleting invoice: $invoice")
        database.withTransaction {
            // First, remove all the invoice products from the invoice.
            database.invoiceProductDao().deleteProducts(invoice.id)

            // Then, remove the invoice.
            database.invoiceDao().delete(invoice.toDto())
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class InvoiceModule {
    @Binds
    abstract fun bindService(impl: InvoiceServiceImpl): InvoiceService
}