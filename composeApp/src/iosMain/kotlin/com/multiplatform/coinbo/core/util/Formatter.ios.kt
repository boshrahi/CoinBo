package com.multiplatform.coinbo.core.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual fun formatFiat(amount: Double, showDecimal: Boolean): String {
  val numberFormatter = NSNumberFormatter()
  numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
  when {
    showDecimal.not() -> {
      numberFormatter.minimumFractionDigits = 0.toULong()
      numberFormatter.maximumFractionDigits = 0.toULong()
    }
    amount >= 0.01 -> {
      numberFormatter.minimumFractionDigits = 2.toULong()
      numberFormatter.maximumFractionDigits = 2.toULong()
    }
    else -> {
      numberFormatter.minimumFractionDigits = 8.toULong()
      numberFormatter.maximumFractionDigits = 8.toULong()
    }
  }
  val formattedAmount = numberFormatter.stringFromNumber(NSNumber(amount))
  return if (formattedAmount != null) {
    "$ $formattedAmount"
  } else {
    ""
  }
}

actual fun formatCoinUnit(amount: Double, symbol: String): String {
  val numberFormatter = NSNumberFormatter()
  numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
  numberFormatter.minimumFractionDigits = 8.toULong()
  numberFormatter.maximumFractionDigits = 8.toULong()
  return numberFormatter.stringFromNumber(NSNumber(amount)) + " $symbol"
}

actual fun formatPercentages(amount: Double): String {
  val numberFormatter = NSNumberFormatter()
  numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
  numberFormatter.minimumFractionDigits = 2.toULong()
  numberFormatter.maximumFractionDigits = 2.toULong()
  val prefix = if (amount >= 0) "+" else ""
  return prefix + numberFormatter.stringFromNumber(NSNumber(amount)) + " %"
}
