package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.DEFAULT_PAGE_SIZE
import com.example.project_shelf.adapter.dto.room.CustomerDto
import com.example.project_shelf.adapter.dto.room.CustomerFtsDto
import com.example.project_shelf.adapter.dto.room.toCustomerFilter
import com.example.project_shelf.adapter.dto.room.toDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.CustomerFilter
import com.example.project_shelf.app.service.CustomerService
import com.example.project_shelf.common.Id
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
) : CustomerService {
    override fun get(): Flow<PagingData<Customer>> {
        Log.d("SERVICE-IMPL", "Finding customers")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) { database.customerDao().select() }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override fun search(value: String): Flow<PagingData<CustomerFilter>> {
        Log.d("SERVICE-IMPL", "Searching customers with: $value")
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.customerFtsDao().match("$value*")
        }.flow.map {
            it.map { dto ->
                CustomerFilter(
                    id = dto.customerId,
                    name = dto.name,
                    businessName = dto.businessName,
                )
            }
        }
    }

    override suspend fun search(id: Id): Customer? {
        Log.d("IMPL", "Searching customer with ID: $id")
        return database.customerDao().search(id)?.toEntity()
    }

    override suspend fun create(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?,
    ): Customer {
        Log.d(
            "SERVICE-IMPL",
            "Creating customer with: $name, $phone, $address, $cityId, $businessName"
        )
        return database.withTransaction {
            // First, create the customer.
            var customerId = database.customerDao().insert(
                CustomerDto(
                    name = name,
                    phone = phone,
                    address = address,
                    cityId = cityId,
                    businessName = businessName,
                )
            )

            // Then, store the FTS value.
            database.customerFtsDao().insert(
                CustomerFtsDto(
                    customerId = customerId,
                    name = name,
                    businessName = businessName,
                )
            )

            Customer(
                id = customerId,
                cityId = cityId,
                name = name,
                phone = phone,
                address = address,
                businessName = businessName,
            )
        }
    }

    override suspend fun update(
        id: Long, name: String, phone: String, address: String, cityId: Long, businessName: String?
    ) {
        Log.d(
            "SERVICE-IMPL",
            "Updating customer with: $id, $name, $phone, $address, $cityId, $businessName"
        )
        database.customerDao().update(
            CustomerDto(
                rowId = id,
                name = name,
                phone = phone,
                address = address,
                businessName = businessName,
                cityId = cityId
            )
        )
    }

    override suspend fun delete() {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Removing all customers")
            database.customerDao().delete()

            Log.d("SERVICE-IMPL", "Removing all customers FTS")
            database.customerFtsDao().delete()
        }
    }

    override suspend fun delete(id: Long) {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Customer[$id]: Removing customer")
            database.customerDao().delete(id)

            Log.d("SERVICE-IMPL", "Customer[$id]: Removing customer FTS")
            database.customerFtsDao().delete(id)
        }
    }

    override suspend fun deletePendingForDeletion() {
        Log.d("SERVICE-IMPL", "Deleting customers pending for deletion.")
        // As we need to remove more than just the entity, we have to first select the items.
        // NOTE:
        //  This might not be the best way of doing this, but we don't expect this to have more than
        //  10 or 100 items at a time.
        database.customerDao().selectPendingForDeletion().forEach {
            delete(it.rowId)
        }
    }

    override suspend fun setPendingForDeletion(id: Long, until: Long) {
        Log.d("SERVICE-IMPL", "Customer[$id]: Setting customer pending for deletion")
        database.customerDao().setPendingForDeletion(id, until)
    }

    override suspend fun unsetPendingForDeletion(id: Long) {
        Log.d("SERVICE-IMPL", "Customer[$id]: Unsetting customer pending for deletion")
        database.customerDao().unsetPendingForDeletion(id)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CustomerModule {
    @Binds
    abstract fun bindService(impl: CustomerServiceImpl): CustomerService
}