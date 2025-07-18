package com.example.project_shelf.adapter.view_model.util

import android.util.Log
import com.example.project_shelf.adapter.ViewModelError

class StringValidator(
    private val required: Boolean = false,
) : Validator<String> {
    override fun validate(value: String): Pair<String?, List<ViewModelError>> {
        val errors = mutableListOf<ViewModelError>()
        if (required && value.isBlank()) {
            errors.add(ViewModelError.BLANK_VALUE)
        }

        // Do any kind of transformation here to clean the input.
        return Pair(value.trim(), errors)
    }
}