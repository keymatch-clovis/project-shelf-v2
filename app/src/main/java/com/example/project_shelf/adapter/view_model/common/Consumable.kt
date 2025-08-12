package com.example.project_shelf.adapter.view_model.common

class Consumable<T>(private var value: T?) {
    fun consume(): T? = value?.also { value = null }
}