package com.example.project_shelf.adapter.presenter

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDto
import com.example.project_shelf.adapter.dto.ui.InvoiceFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceWithCustomerDto
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.InvoiceRepository
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceUseCase
import com.example.project_shelf.app.use_case.invoice.GetInvoicesWithCustomersUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoicePresenter @Inject constructor(
    private val getInvoicesWithCustomersUseCase: GetInvoicesWithCustomersUseCase,
    private val createInvoiceUseCase: CreateInvoiceUseCase,
) : InvoiceRepository {
    override fun get(): Flow<PagingData<InvoiceWithCustomerDto>> {
        Log.d("PRESENTER", "Getting invoices")
        return getInvoicesWithCustomersUseCase.exec().map {
            it.map { dto ->
                InvoiceWithCustomerDto(
                    invoice = InvoiceDto(
                        id = dto.invoice.id,
                        number = dto.invoice.number,
                        // TODO: Use correct format here.
                        date = dto.invoice.date.time,
                        remainingUnpaidBalance = dto.invoice.remainingUnpaidBalance,
                    ),
                    customer = CustomerDto(
                        id = dto.customer.id,
                        name = dto.customer.name,
                        phone = dto.customer.phone,
                        address = dto.customer.address,
                        // TODO: Use correct format here.
                        businessName = dto.customer.businessName ?: "",
                    ),
                )
            }
        }
    }

    override fun search(value: String): Flow<PagingData<InvoiceFilterDto>> {
        Log.d("PRESENTER", "Searching invoices with: $value")
        return flowOf(PagingData.empty())
    }

    override suspend fun update(dto: InvoiceDto): InvoiceDto {
        TODO("Not yet implemented")
    }

    override suspend fun create(
        customer: CustomerDto, products: List<ProductDto>
    ): InvoiceDto {
        TODO("Not yet implemented")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class InvoicePresenterModule {
    @Binds
    abstract fun bind(presenter: InvoicePresenter): InvoiceRepository
}