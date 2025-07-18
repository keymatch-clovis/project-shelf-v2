package com.example.project_shelf.adapter.view_model.util

import com.example.project_shelf.adapter.ViewModelError

class IntValidator(
    private val required: Boolean = false,
) : Validator<Int> {
    override fun validate(input: String): Pair<Int?, List<ViewModelError>> {
        val errors = mutableListOf<ViewModelError>()
        val transformed = input.toIntOrNull()

        if (required) {
            if (input.isBlank()) {
                errors.add(ViewModelError.BLANK_VALUE)
            }
            if (transformed == null) {
                errors.add(ViewModelError.INVALID_INTEGER_VALUE)
            }
        }

        if (input.isNotBlank()) {
            if (transformed == null) {
                errors.add(ViewModelError.INVALID_INTEGER_VALUE)
            }
        }

        return Pair(transformed, errors)
    }
}