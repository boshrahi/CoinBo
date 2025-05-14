package com.multiplatform.coinbo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.multiplatform.coinbo.coins.presentation.CoinsListScreen
import com.multiplatform.coinbo.theme.CoinBoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  CoinBoTheme {
    CoinsListScreen {
    }
  }
}
