package com.example.project_shelf.common

import org.joda.money.CurrencyUnit
import java.util.Locale

object DefaultConfig {
    // FIXME: Here, we can declare a list of countries available for currency units. For now, we'll
    //  leave this value.
    fun getDefaultCurrencyUnit(): CurrencyUnit {
        return try {
            CurrencyUnit.of(Locale.getDefault())
        } catch (e: IllegalArgumentException) {
            CurrencyUnit.USD
        }
    }
}