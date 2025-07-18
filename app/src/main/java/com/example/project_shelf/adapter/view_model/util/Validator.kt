package com.example.project_shelf.adapter.view_model.util

import com.example.project_shelf.adapter.ViewModelError

interface Validator<I> {
    fun validate(value: String): Pair<I?, List<ViewModelError>>
}