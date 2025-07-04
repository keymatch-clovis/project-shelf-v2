package com.example.project_shelf.adapter.view_model

import com.example.project_shelf.adapter.ViewModelError
import java.math.BigDecimal

fun String.toBigDecimalOrZero(): BigDecimal = this.toBigDecimalOrNull() ?: BigDecimal.ZERO
fun String.toIntOrZero(): Int = this.toIntOrNull() ?: 0

fun String.validateString(required: Boolean = false): List<ViewModelError> {
    if (required && this.isBlank()) {
        return listOf(ViewModelError.BLANK_VALUE)
    }
    return emptyList()
}

fun String.validateBigDecimal(required: Boolean = false): List<ViewModelError> {
    val value = this.toBigDecimalOrNull()
    val errors = this.validateString().toMutableList()

    // If required, check the value is correct.
    if (required && value == null) {
        errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
    }
    // If not empty, check the value is correct.
    if (this.isNotEmpty() && value == null) {
        errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
    }
    return errors
}

fun String.validateInt(required: Boolean = false): List<ViewModelError> {
    val value = this.toIntOrNull()
    val errors = this.validateString().toMutableList()

    // If required, check the value is correct.
    if (required && value == null) {
        errors.add(ViewModelError.INVALID_INTEGER_VALUE)
    }
    // If not empty, check the value is correct.
    if (this.isNotEmpty() && value == null) {
        errors.add(ViewModelError.INVALID_INTEGER_VALUE)
    }
    return errors
}