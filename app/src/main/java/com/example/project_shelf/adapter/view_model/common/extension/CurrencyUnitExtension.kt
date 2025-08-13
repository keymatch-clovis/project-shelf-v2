package com.example.project_shelf.adapter.view_model.common.extension

import org.joda.money.CurrencyUnit
import java.util.Locale

fun currencyUnitFromDefaultLocale(): CurrencyUnit {
    return try {
        CurrencyUnit.of(Locale.getDefault())
    } catch (_: IllegalArgumentException) {
        CurrencyUnit.USD
    }
}