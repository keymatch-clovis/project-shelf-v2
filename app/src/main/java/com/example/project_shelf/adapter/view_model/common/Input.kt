package com.example.project_shelf.adapter.view_model.common

import com.example.project_shelf.adapter.ViewModelError

data class Input<T>(
    val value: T? = null,
    val errors: List<ViewModelError> = emptyList(),
)