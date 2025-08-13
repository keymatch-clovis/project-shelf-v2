package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.DEFAULT_PAGE_SIZE
import com.example.project_shelf.adapter.dto.objectbox.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.objectbox.InvoiceDraftProductDto
import com.example.project_shelf.adapter.dto.room.InvoiceDto
import com.example.project_shelf.adapter.dto.room.InvoiceFtsDto
import com.example.project_shelf.adapter.dto.room.InvoiceProductDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceDraft
import com.example.project_shelf.app.entity.InvoiceFilter
import com.example.project_shelf.app.entity.InvoicePopulated
import com.example.project_shelf.app.entity.InvoiceWithCustomer
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.app.service.model.CreateInvoiceDraftInput
import com.example.project_shelf.app.service.model.CreateInvoiceInput
import com.example.project_shelf.app.service.model.EditInvoiceDraftInput
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.objectbox.BoxStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

class InvoiceServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
    private val boxStore: BoxStore,
) : InvoiceService {
    override fun get(): Flow<PagingData<Invoice>> {
        Log.d("SERVICE-IMPL", "Getting invoices")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            database
                .invoiceDao()
                .select()
        }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override fun getWithCustomer(): Flow<PagingData<InvoiceWithCustomer>> {
        Log.d("SERVICE-IMPL", "Getting invoices with customer")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            database
                .invoiceDao()
                .selectWithCustomer()
        }.flow.map {
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
        return database
            .invoiceDao()
            .getMaxNumber()
    }

    override fun search(value: String): Flow<PagingData<InvoiceFilter>> {
        Log.d("SERVICE-IMPL", "Searching invoices with: $value")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database
                .invoiceFtsDao()
                .match("$value*")
        }.flow.map { it.map { dto -> dto.toEntity() } }
    }

    override suspend fun create(input: CreateInvoiceInput): Long {
        Log.d("SERVICE-IMPL", "Creating invoice with: $input")
        return database.withTransaction {
            // Create the invoice
            val invoiceId = database
                .invoiceDao()
                .insert(
                    InvoiceDto(
                        number = input.number, customerId = input.customerId, date = Date().time,
                        // TODO: fixthis
                        remainingUnpaidBalance = 0
                    )
                )

            // Create the invoice products.
            database
                .invoiceProductDao()
                .insert(
                    *input.products
                        .map {
                            InvoiceProductDto(
                                invoiceId = invoiceId,
                                productId = it.productId,
                                count = it.count,
                                price = it.price.amountMinorLong,
                            )
                        }
                        .toTypedArray())

            // Get the customer to assign the search values.
            val customer = database
                .customerDao()
                .select(input.customerId)

            // Finally, store the FTS value.
            database
                .invoiceFtsDao()
                .insert(
                    InvoiceFtsDto(
                        invoiceId = invoiceId,
                        number = input.number,
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
            database
                .invoiceProductDao()
                .delete()

            Log.d("SERVICE-IMPL", "Deleting all invoices")
            database
                .invoiceDao()
                .delete()

            Log.d("SERVICE-IMPL", "Deleting all invoices FTS")
            database
                .invoiceFtsDao()
                .delete()
        }
    }

    override suspend fun delete(id: Long) {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Invoice[$id]: Deleting invoice products")
            database
                .invoiceProductDao()
                .deleteProducts(id)

            Log.d("SERVICE-IMPL", "Invoice[$id]: Deleting invoice")
            database
                .invoiceDao()
                .delete(id)

            Log.d("SERVICE-IMPL", "Invoice[$id]: Deleting invoice FTS")
            database
                .invoiceFtsDao()
                .delete(id)
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

    override suspend fun getDrafts(): List<InvoiceDraft> {
        Log.d("SERVICE-IMPL", "Getting invoice drafts")
        return boxStore.boxFor(InvoiceDraftDto::class.java).all.map {
            InvoiceDraft(
                id = it.id,
                date = it.date,
                remainingUnpaidBalance = it.remainingUnpaidBalance,
                products = it.products.map { it.toEntity() },
                customerId = it.customerId,
            )
        }
    }

    override suspend fun createDraft(input: CreateInvoiceDraftInput): Long {
        Log.d("IMPL", "Creating draft with: $input")
        val invoiceDraft = InvoiceDraftDto(
            date = input.date,
            remainingUnpaidBalance = input.remainingUnpaidBalance,
            customerId = input.customerId,
        )

        input.products.forEach {
            val dto = InvoiceDraftProductDto(
                productId = it.productId,
                count = it.count,
                price = it.price.amountMinorLong,
            )

            // https://docs.objectbox.io/relations#updating-toone
            dto.invoiceDraft.setAndPutTarget(invoiceDraft)

            invoiceDraft.products.add(dto)
        }

        return boxStore
            .boxFor(InvoiceDraftDto::class.java)
            .put(invoiceDraft)
    }

    override suspend fun editDraft(input: EditInvoiceDraftInput) {
        // TODO: Maybe move all this to the application layer?
        //
        // > It seems we need to update both relationships, like a RDBMS, not like mongo, silly me.
        // https://docs.objectbox.io/relations#updating-tomany

        val box = boxStore.boxFor(InvoiceDraftDto::class.java)
        val invoiceDraft = box.get(input.draftId)

        invoiceDraft.customerId = input.customerId

        // FIXME: This can be improved, by filtering the ones that are not in this list, and
        //  removing the ones that are in the DTO and not in this list. But for now I think it is
        //  fine.
        // https://docs.objectbox.io/relations#one-to-many-1-n
        invoiceDraft.products
            .map { it.id }
            .toLongArray()
            .let {
                boxStore
                    .boxFor(InvoiceDraftProductDto::class.java)
                    .remove(*it)
            }
        // FIXME: This is also not completely good, as we are deleting all the products, and then
        //  adding them again.
        invoiceDraft.products.clear()

        input.products.forEach {
            val dto = InvoiceDraftProductDto(
                productId = it.productId,
                count = it.count,
                price = it.price.amountMinorLong,
            )

            // https://docs.objectbox.io/relations#updating-toone
            dto.invoiceDraft.setAndPutTarget(invoiceDraft)

            invoiceDraft.products.add(dto)
        }

        box.put(invoiceDraft)
    }

    override suspend fun deleteDrafts(vararg ids: Long) {
        boxStore
            .boxFor(InvoiceDraftDto::class.java)
            .remove(*ids)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class InvoiceServiceImplModule {
    @Binds
    abstract fun bind(impl: InvoiceServiceImpl): InvoiceService
}