package com.example.project_shelf.adapter.view_model.util.validator

import com.example.project_shelf.adapter.ViewModelError

class StringValidator(
    private val required: Boolean = false,
) : Validator<String, String> {
    override fun validate(value: String?): Pair<String?, List<ViewModelError>> {
        val errors = mutableListOf<ViewModelError>()
        if (required) {
            if (value != null) {
                if (value.isBlank()) {
                    errors.add(ViewModelError.BLANK_VALUE)
                }
            } else {
                errors.add(ViewModelError.NULL_VALUE)
            }
        }

        // Do any kind of transformation here to clean the input.
        return Pair(value?.trim(), errors)
    }
}