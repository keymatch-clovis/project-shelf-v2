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
import com.example.project_shelf.adapter.dto.room.toCustomer
import com.example.project_shelf.adapter.dto.room.toCustomerFilter
import com.example.project_shelf.adapter.dto.room.toDto
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.CustomerFilter
import com.example.project_shelf.app.service.CustomerService
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
    override fun getCustomers(): Flow<PagingData<Customer>> {
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) { database.customerDao().select() }.flow.map {
            it.map { dto -> dto.toCustomer() }
        }
    }

    override fun getCustomers(searchValue: String): Flow<PagingData<CustomerFilter>> {
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.customerFtsDao().match("$searchValue*")
        }.flow.map {
            it.map { dto -> dto.toCustomerFilter() }
        }
    }

    override suspend fun create(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?,
    ) {
        Log.d(
            "SERVICE-IMPL",
            "Creating customer with: $name, $phone, $address, $cityId, $businessName"
        )
        database.withTransaction {
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
        }
    }

    override suspend fun removeAll() {
        Log.d("SERVICE-IMPL", "Removing all customers")
        database.customerDao().delete()
    }

    override suspend fun remove(customer: Customer) {
        Log.d("SERVICE-IMPL", "Removing customer: $customer")
        database.customerDao().delete(customer.toDto())
    }

    override suspend fun update(customer: Customer) {
        Log.d("SERVICE-IMPL", "Updating customer: $customer")
        database.customerDao().update(customer.toDto())
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CustomerModule {
    @Binds
    abstract fun bindService(impl: CustomerServiceImpl): CustomerService
}