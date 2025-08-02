package com.example.project_shelf.framework.ui.util

import android.icu.text.DecimalFormatSymbols
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyFormatterBuilder
import java.math.RoundingMode
import java.util.Locale

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val decimalValue = text.text.toBigDecimalOrNull()
        if (decimalValue == null) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val currency = CurrencyUnit.of("COP")
        val money = Money.of(
            currency, decimalValue.setScale(
                currency.decimalPlaces, RoundingMode.FLOOR
            )
        )

        val formatter = MoneyFormatterBuilder().appendAmountLocalized().toFormatter()
        val formatted = formatter.withLocale(Locale.getDefault()).print(money)

        val groupingSeparator =
            DecimalFormatSymbols.getInstance(Locale.getDefault()).groupingSeparator
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var rawIndex = 0
                var formattedIndex = 0
                var groupingCount = 0

                while (rawIndex < offset && formattedIndex < formatted.length) {
                    if (formatted[formattedIndex] != groupingSeparator) {
                        ++rawIndex
                    } else {
                        ++groupingCount
                    }
                    ++formattedIndex
                }

                return (offset + groupingCount).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return (offset).coerceIn(0, text.text.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}