package com.example.project_shelf.framework.ui

import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.R

fun ViewModelError.getStringResource(): Int {
    return when (this) {
        ViewModelError.BLANK_VALUE -> R.string.err_value_required
        ViewModelError.INVALID_DECIMAL_VALUE -> R.string.err_decimal_required
        ViewModelError.INVALID_INTEGER_VALUE -> R.string.err_integer_required
        ViewModelError.PRODUCT_NAME_TAKEN -> R.string.err_product_name_taken
    }
}