package com.multiplatform.coinbo.core.util

import android.icu.text.DecimalFormat

/**
 * inside of compose main we have access to java libs
 * but other packages are not allowed
 * */
actual fun formatFiat(amount: Double, showDecimal: Boolean): String {
  val formatter = when {
    showDecimal.not() -> DecimalFormat("#,###")
    amount >= 1 -> DecimalFormat("#,###.00")
    amount >= 0.01 -> DecimalFormat("0.00")
    else -> DecimalFormat("0.00000000")
  }
  return "$ " + formatter.format(amount)
}

actual fun formatCoinUnit(amount: Double, symbol: String): String {
  return DecimalFormat("0.00000000").format(amount) + " $symbol"
}

actual fun formatPercentages(amount: Double): String {
  val prefix = if (amount >= 0) "+" else ""
  return prefix + DecimalFormat("0.00").format(amount) + " %"
}
