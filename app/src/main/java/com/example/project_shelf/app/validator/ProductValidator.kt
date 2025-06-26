package com.example.project_shelf.app.validator

import com.example.project_shelf.common.BlankValueException
import java.math.BigDecimal
import java.math.BigInteger

fun validateProductName(value: String): Result<String> = runCatching {
    if (value.isBlank()) throw BlankValueException()
    // TODO: Maybe add that the value has a max length?
    return@runCatching value
}

fun validateProductPrice(value: String): Result<BigInteger> = runCatching {
    if (value.isBlank()) throw BlankValueException()
    // FIXME: This is incorrect if we want to use the app for other currencies. We should use here
    // an interface or something like that, but for now it is not necessary.
    return@runCatching (value.toBigDecimal() * BigDecimal(100)).toBigInteger()
}

fun validateProductCount(value: String): Result<Int> = runCatching {
    if (value.isBlank()) throw BlankValueException()
    return@runCatching value.toInt()
}
