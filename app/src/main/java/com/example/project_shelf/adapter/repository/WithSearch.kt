package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface WithSearch<T : Any> {
    fun search(value: String): Flow<PagingData<T>>
}