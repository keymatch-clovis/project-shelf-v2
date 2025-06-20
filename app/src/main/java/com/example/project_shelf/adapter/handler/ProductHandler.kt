package com.example.project_shelf.adapter.handler

import android.util.Log
import javax.inject.Inject

class ProductHandler @Inject constructor() {
    suspend fun create(name: String, price: String = "", count: String = "") {
        Log.d("HANDLER", "Creating Product With: $name, $price, $count")
    }
}