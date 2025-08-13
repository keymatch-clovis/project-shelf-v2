package com.example.project_shelf.adapter.view_model.common.extension

import org.joda.money.Money
import org.joda.money.format.MoneyFormatterBuilder

fun Money.toFormattedString(): String = MoneyFormatterBuilder()
    .appendCurrencySymbolLocalized()
    .appendAmountLocalized()
    .toFormatter()
    .let {
        val withoutCents = this.withAmount(this.amountMajor)
        it.print(withoutCents)
    }
