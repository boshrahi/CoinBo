package com.multiplatform.coinbo.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CoinBoColorsPalette(
  val profitGreen: Color = Color.Unspecified,
  val lossRed: Color = Color.Unspecified,
)
val ProfitGreenColor = Color(color = 0xFF32de84)
val LossRedColor = Color(color = 0xFFD2122E)

val DarkProfitGreenColor = Color(color = 0xFF32de84)
val DarkLossRedColor = Color(color = 0xFFD2122E)

val LightCoinBoColorsPalette = CoinBoColorsPalette(
  profitGreen = ProfitGreenColor,
  lossRed = LossRedColor,
)

val DarkCoinBoColorsPalette = CoinBoColorsPalette(
  profitGreen = DarkProfitGreenColor,
  lossRed = DarkLossRedColor,
)

val LocalCoinBoColorsPalette = compositionLocalOf { CoinBoColorsPalette() }
