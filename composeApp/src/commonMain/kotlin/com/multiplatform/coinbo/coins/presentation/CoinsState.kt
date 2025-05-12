package com.multiplatform.coinbo.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

/**
 * the object is stable and the properties wont change unexpectedly
 * we use UiCoinListItem to represent the coin list item in the UI with formatted values
 * it is different from the domain model
 * */
@Stable
data class CoinsState(
  val error: StringResource? = null,
  val coins: List<UiCoinListItem> = emptyList(),
)
