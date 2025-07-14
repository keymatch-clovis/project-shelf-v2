package com.example.project_shelf.adapter.view_model.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.project_shelf.adapter.dto.ui.CityDto
import com.example.project_shelf.adapter.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val repository: CityRepository,
) : ViewModel() {
    private val _result: MutableStateFlow<PagingData<CityDto>> =
        MutableStateFlow(PagingData.empty())
    var result = _result.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        viewModelScope.launch {
            _query.debounce(300).distinctUntilChanged().flatMapLatest {
                repository.search(it.toString())
            }.cachedIn(viewModelScope).collectLatest {
                _result.value = it
            }
        }
    }

    fun updateQuery(value: String) {
        _query.update { value }
    }
}