package com.multiplatform.coinbo

import androidx.compose.ui.window.ComposeUIViewController
import com.multiplatform.coinbo.di.initKoin

fun MainViewController() = ComposeUIViewController(
  configure = {
    initKoin()
  },
) { App() }
