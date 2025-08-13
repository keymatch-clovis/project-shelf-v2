package com.example.project_shelf.adapter.view_model.common.extension

import com.example.project_shelf.common.DefaultConfig
import org.joda.money.Money
import java.math.RoundingMode

fun String.toMoney(): Money {
    val currencyUnit = DefaultConfig.getDefaultCurrencyUnit()

    return this
        .toBigDecimal()
        .setScale(currencyUnit.decimalPlaces, RoundingMode.FLOOR)
        .let { Money.of(currencyUnit, it) }
}

fun String?.toMoneyOrZero(): Money {
    val currencyUnit = DefaultConfig.getDefaultCurrencyUnit()

    return this
        ?.toBigDecimal()
        ?.setScale(currencyUnit.decimalPlaces, RoundingMode.FLOOR)
        ?.let { Money.of(currencyUnit, it) }
        .let { it ?: Money.zero(currencyUnit) }
}
