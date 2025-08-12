package com.example.project_shelf.adapter.view_model.common.validator

import com.example.project_shelf.adapter.ViewModelError

fun Any?.validateObject(required: Boolean = false): List<ViewModelError> {
    val errors = mutableListOf<ViewModelError>()
    if (required) {
        if (this == null) {
            errors.add(ViewModelError.NULL_VALUE)
        }
    }

    return errors
}