package com.example.project_shelf.framework.ui.common.extension

import org.joda.money.Money
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

fun Money.toFormattedString(
    withSymbol: Boolean = false,
    withDecimals: Boolean = false,
): String {
    val locale = Locale.getDefault()

    return NumberFormat
        .getCurrencyInstance(locale)
        .let { it as DecimalFormat }
        .apply {
            if (!withDecimals) {
                maximumIntegerDigits = 0
                minimumIntegerDigits = 0
            }

            if (!withSymbol) {
                val symbols = DecimalFormatSymbols.getInstance(locale)
                symbols.currencySymbol = ""

                decimalFormatSymbols = symbols
            }
        }
        .format(this.amount)
}