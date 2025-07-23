package com.example.project_shelf.adapter.presenter

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.app.use_case.customer.CreateCustomerUseCase
import com.example.project_shelf.app.use_case.customer.DeleteAllCustomersUseCase
import com.example.project_shelf.app.use_case.customer.GetCustomersUseCase
import com.example.project_shelf.app.use_case.customer.SearchCustomersUseCase
import com.example.project_shelf.app.use_case.customer.SetCustomerPendingForDeletionUseCase
import com.example.project_shelf.app.use_case.customer.UnsetCustomerPendingForDeletionUseCase
import com.example.project_shelf.app.use_case.customer.UpdateCustomerUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerPresenter @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase,
    private val searchCustomersUseCase: SearchCustomersUseCase,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    private val createCustomerUseCase: CreateCustomerUseCase,
    private val setCustomerPendingForDeletionUseCase: SetCustomerPendingForDeletionUseCase,
    private val unsetCustomerPendingForDeletionUseCase: UnsetCustomerPendingForDeletionUseCase,
    private val deleteAllCustomersUseCase: DeleteAllCustomersUseCase,
) : CustomerRepository {
    override fun find(): Flow<PagingData<CustomerDto>> {
        Log.d("PRESENTER", "Finding customers")
        return getCustomersUseCase.exec().map {
            it.map { customer -> customer.toDto() }
        }
    }

    override fun search(value: String): Flow<PagingData<CustomerFilterDto>> {
        Log.d("PRESENTER", "Searching customers with: $value")
        return searchCustomersUseCase.exec(value).map {
            it.map { filter ->
                CustomerFilterDto(
                    id = filter.id,
                    name = filter.name,
                    businessName = filter.businessName,
                )
            }
        }
    }

    override suspend fun update(
        id: Long, name: String, phone: String, address: String, cityId: Long, businessName: String,
    ) {
        Log.d(
            "PRESENTER",
            "Updating customer with: $id, $name, $phone, $address, $cityId, $businessName"
        )
        return updateCustomerUseCase.exec(id, name, phone, address, cityId, businessName)
    }

    override suspend fun create(
        name: String, phone: String, address: String, cityId: Long, businessName: String
    ): CustomerDto {
        Log.d(
            "PRESENTER",
            "Creating customer with: $name, $phone, $address, $cityId, $businessName"
        )
        return createCustomerUseCase.exec(name, phone, address, cityId, businessName).toDto()
    }

    override suspend fun setPendingForDeletion(id: Long) {
        Log.d("PRESENTER", "Customer[$id]: Setting pending for deletion")
        setCustomerPendingForDeletionUseCase.exec(id)
    }

    override suspend fun unsetPendingForDeletion(id: Long) {
        Log.d("PRESENTER", "Customer[$id]: Unsetting pending for deletion")
        unsetCustomerPendingForDeletionUseCase.exec(id)
    }

    override suspend fun deleteAll() {
        Log.d("PRESENTER", "Deleting all customers")
        deleteAllCustomersUseCase.exec()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CustomerPresenterModel {
    @Binds
    abstract fun bind(presenter: CustomerPresenter): CustomerRepository
}