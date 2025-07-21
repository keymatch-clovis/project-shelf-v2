package com.example.project_shelf.adapter.view_model.util

import com.example.project_shelf.adapter.ViewModelError
import java.math.BigDecimal

class BigDecimalValidator(
    private val required: Boolean = false,
) : Validator<BigDecimal> {
    override fun validate(value: String): Pair<BigDecimal?, List<ViewModelError>> {
        val errors = mutableListOf<ViewModelError>()
        val transformed = value.toBigDecimalOrNull()

        if (required) {
            if (value.isBlank()) {
                errors.add(ViewModelError.BLANK_VALUE)
            }
            if (transformed == null) {
                errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
            }
        }

        if (value.isNotBlank() && transformed == null) {
            errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
        }

        return Pair(transformed, errors)
    }
}