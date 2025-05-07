@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.multiplatform.coinbo.core.network

import com.multiplatform.coinbo.BuildConfig

actual object ApiKeys {
  actual val COIN_RANKING_API_KEY: String
    get() = BuildConfig.COIN_RANKING_API_KEY
}
