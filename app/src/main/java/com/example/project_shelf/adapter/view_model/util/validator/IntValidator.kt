package com.example.project_shelf.adapter.view_model.util.validator

import com.example.project_shelf.adapter.ViewModelError

class IntValidator(
    private val required: Boolean = false,
) : Validator<String, Int> {
    override fun validate(value: String?): Pair<Int?, List<ViewModelError>> {
        val errors = mutableListOf<ViewModelError>()
        val transformed = value?.toIntOrNull()

        if (required) {
            if (value != null) {
                if (value.isBlank()) {
                    errors.add(ViewModelError.BLANK_VALUE)
                }
            } else {
                errors.add(ViewModelError.NULL_VALUE)
            }
            if (transformed == null) {
                errors.add(ViewModelError.INVALID_INTEGER_VALUE)
            }
        }

        // If the value is not null, we have to check the inside of the string, to see if it is what
        // we expect.
        if (value != null) {
            if (value.isNotBlank()) {
                if (transformed == null) {
                    errors.add(ViewModelError.INVALID_INTEGER_VALUE)
                }
            }
        }

        return Pair(transformed, errors)
    }
}