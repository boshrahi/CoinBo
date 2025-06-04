package com.multiplatform.coinbo.trade.presentation.common.component

import androidx.compose.ui.text.input.OffsetMapping

/**
 * This class is responsible for mapping offsets from the original text to the formatted version.
 * */
class CurrencyOffsetMapping(originalText: String, formattedText: String) : OffsetMapping {
  private val originalLength = originalText.length
  private val indexes = findDigitIndexes(originalText, formattedText)

  // build a list of where each digit from originalText ended up in formattedText.
  private fun findDigitIndexes(firstString: String, secondString: String): List<Int> {
    val digitIndexes = mutableListOf<Int>()
    var currentIndex = 0
    for (digit in firstString) {
      val index = secondString.indexOf(digit, currentIndex)
      if (index != -1) {
        digitIndexes.add(index)
        currentIndex = index + 1
      } else {
        return emptyList()
      }
    }
    return digitIndexes
  }

  // Given a cursor position in the raw string (offset from 0..originalLength),
  // returns where that cursor should appear in the formatted string.
  override fun originalToTransformed(offset: Int): Int {
    if (offset >= originalLength) {
      return indexes.last() + 1
    }
    return indexes[offset] // original 123 formatted $123 indexes = [1, 2, 3] index:0 -> $
  }

  // Given a cursor position in the formatted string, finds which original-text index that corresponds to.
  override fun transformedToOriginal(offset: Int): Int {
    return indexes.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength
  }
}
