package com.example.project_shelf.adapter.view_model.customer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel()
class CustomersViewModel @Inject constructor(
    repository: CustomerRepository,
) : ViewModel() {
    var customers: Flow<PagingData<CustomerDto>> = repository.find()
    var lazyListState: LazyListState by mutableStateOf(LazyListState(0, 0))
}