package com.example.project_shelf.framework.ui.common

import android.icu.text.DecimalFormatSymbols
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.project_shelf.adapter.view_model.common.extension.toMoney
import com.example.project_shelf.framework.ui.common.extension.toFormattedString
import java.math.BigDecimal
import java.util.Currency
import java.util.Locale

class CurrencyVisualTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val decimalValue = text.text.toBigDecimalOrNull()
        if (decimalValue == null) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        if (decimalValue.stripTrailingZeros() == BigDecimal.ZERO) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        if (decimalValue.scale() > Currency.getInstance(Locale.getDefault()).defaultFractionDigits) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val money = text.text.toMoney()
        val formatted = money.toFormattedString(withDecimals = true)

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