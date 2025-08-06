package com.example.project_shelf.adapter.view_model.util

class Consumable<T>(private var value: T?) {
    /**
     * @throws NullPointerException as we want to ensure the program has the correct design when
     * using this consumable.
     */
    fun consume(): T = value!!.also { value = null }
}