package com.example.project_shelf.adapter.view_model.common

import java.math.BigDecimal

fun String.toBigDecimalOrZero(): BigDecimal = this.toBigDecimalOrNull() ?: BigDecimal.ZERO
fun String.toIntOrZero(): Int = this.toIntOrNull() ?: 0