package com.multiplatform.coinbo.trade.presentation.common.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.multiplatform.coinbo.core.util.formatFiat

/**
 * change how text is displayed without altering the actual underlying value
 * An AnnotatedString — the text that will actually be rendered on screen.
 *
 * An OffsetMapping — a mapping from “cursor positions in the original string” to “cursor positions
 * in the transformed string,” so that cursor movement, selection, and editing still
 * work correctly even though the displayed characters might have shifted or added punctuation, etc.
 * */
private class CurrencyVisualTransformation : VisualTransformation {

  override fun filter(text: AnnotatedString): TransformedText {
    val originalText = text.text.trim()
    if (originalText.isEmpty()) {
      // identity transformation shows the original text without any changes
      return TransformedText(text, OffsetMapping.Identity)
    }
    if (originalText.isNumeric().not()) {
      // identity transformation shows the original text without any changes
      return TransformedText(text, OffsetMapping.Identity)
    }
    val formattedText = formatFiat(
      amount = originalText.toDouble(),
      showDecimal = false,
    )
    return TransformedText(
      // annotated a string is a specialized character sequence optimized for compose,
      // allowing for text styling and efficient rendering.
      AnnotatedString(formattedText),
      CurrencyOffsetMapping(originalText, formattedText),
    )
  }
}

@Composable
fun rememberCurrencyVisualTransformation(): VisualTransformation {
  val inspectionMode = LocalInspectionMode.current
  return remember {
    if (inspectionMode) {
      VisualTransformation.None
    } else {
      CurrencyVisualTransformation()
    }
  }
}

private fun String.isNumeric(): Boolean {
  return this.all { char -> char.isDigit() }
}
