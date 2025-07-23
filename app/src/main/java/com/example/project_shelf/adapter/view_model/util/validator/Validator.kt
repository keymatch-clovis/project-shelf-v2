package com.example.project_shelf.adapter.view_model.util.validator

import com.example.project_shelf.adapter.ViewModelError

interface Validator<I, J> {
    fun validate(value: I?): Pair<J?, List<ViewModelError>>
}