package com.example.project_shelf.adapter.view_model.util

import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.view_model.util.validator.Validator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Input<I, J>(
    initialValue: I?,
    private val validator: Validator<I, J>,
) {
    private val _rawValue = MutableStateFlow<I?>(null)
    val rawValue = _rawValue.asStateFlow()

    private val _errors = MutableStateFlow(emptyList<ViewModelError>())
    val errors = _errors.asStateFlow()

    private val _cleanValue = MutableStateFlow<J?>(null)
    val cleanValue = _cleanValue.asStateFlow()

    init {
        // When starting the input, update with the initial value, to get any errors related to the
        // passed data.
        update(initialValue)
    }

    fun update(value: I?) {
        _rawValue.update { value }
        clearErrors()

        val (clean, errors) = validator.validate(value)
        if (errors.isEmpty()) {
            _cleanValue.update { clean }
        } else {
            _cleanValue.update { null }
            addErrors(*errors.toTypedArray())
        }
    }

    fun addErrors(vararg errors: ViewModelError) = _errors.update { it + errors }
    fun clearErrors() = _errors.update { emptyList() }
}