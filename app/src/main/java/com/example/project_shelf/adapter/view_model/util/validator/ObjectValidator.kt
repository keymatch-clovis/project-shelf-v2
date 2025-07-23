package com.example.project_shelf.adapter.view_model.util.validator

import com.example.project_shelf.adapter.ViewModelError


// With this validator we want to validate the object directly, so we expect an object in, and we
// return the same object back.
class ObjectValidator<T>(
    private val required: Boolean = false,
) : Validator<T, T> {
    override fun validate(value: T?): Pair<T?, List<ViewModelError>> {
        val errors = mutableListOf<ViewModelError>()
        if (required) {
            if (value == null) {
                errors.add(ViewModelError.NULL_VALUE)
            }
        }

        return Pair(value, errors)
    }
}