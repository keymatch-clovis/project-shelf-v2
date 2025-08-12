package com.example.project_shelf.adapter.view_model.common.validator

import com.example.project_shelf.adapter.ViewModelError

fun String?.validateString(required: Boolean = false): List<ViewModelError> {
    val errors = mutableListOf<ViewModelError>()
    if (required) {
        if (this != null) {
            if (this.isBlank()) {
                errors.add(ViewModelError.BLANK_VALUE)
            }
        } else {
            errors.add(ViewModelError.NULL_VALUE)
        }
    }

    return errors
}