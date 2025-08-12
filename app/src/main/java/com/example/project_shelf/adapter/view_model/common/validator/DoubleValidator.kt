package com.example.project_shelf.adapter.view_model.common.validator

import com.example.project_shelf.adapter.ViewModelError

fun String?.validateDouble(required: Boolean = false): List<ViewModelError> {
    val errors = mutableListOf<ViewModelError>()
    val transformed = this?.toDoubleOrNull()

    if (required) {
        if (this != null) {
            if (this.isBlank()) {
                errors.add(ViewModelError.BLANK_VALUE)
            }
        } else {
            errors.add(ViewModelError.NULL_VALUE)
        }
        if (transformed == null) {
            errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
        }
    }

    // If the this is not null, we have to check the inside of the string, to see if it is what
    // we expect.
    if (this != null) {
        if (this.isNotBlank() && transformed == null) {
            errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
        }
    }

    return errors
}