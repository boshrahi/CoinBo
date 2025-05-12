package com.multiplatform.coinbo.coins.domain.model

import com.multiplatform.coinbo.core.domain.coin.Coin

data class CoinModel(
  val coin: Coin,
  val price: Double,
  val change: Double,
)
