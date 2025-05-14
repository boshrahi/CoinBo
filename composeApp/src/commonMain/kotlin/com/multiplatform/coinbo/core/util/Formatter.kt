package com.multiplatform.coinbo.core.util

/**
 * working with decimals, precision, string formatting differs in iOS and Android
 * */

expect fun formatFiat(amount: Double, showDecimal: Boolean = true): String

expect fun formatCoinUnit(amount: Double, symbol: String): String

expect fun formatPercentages(amount: Double): String
