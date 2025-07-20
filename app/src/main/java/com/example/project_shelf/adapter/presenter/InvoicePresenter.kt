package com.example.project_shelf.adapter.presenter

import android.icu.util.Currency
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDto
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.InvoiceRepository
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceUseCase
import com.example.project_shelf.app.use_case.invoice.GetInvoicesUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoicePresenter @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val createInvoiceUseCase: CreateInvoiceUseCase,
) : InvoiceRepository {
    override fun get(): Flow<PagingData<InvoiceDto>> {
        Log.d("PRESENTER", "Getting invoices")
        return getInvoicesUseCase.exec().map {
            // TODO:
            //  We can get the currency from a configuration option or something, but for now we'll
            //  leave it hard coded.
            it.map { dto -> dto.toDto(Currency.getInstance("COP")) }
        }
    }

    override fun search(value: String): Flow<PagingData<InvoiceDto>> {
        Log.d("PRESENTER", "Searching invoices with: $value")
        TODO("Not yet implemented")
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