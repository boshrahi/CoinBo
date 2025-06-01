package com.multiplatform.coinbo.portfolio.domain

import com.multiplatform.coinbo.core.domain.coin.Coin

data class PortfolioCoinModel(
  val coin: Coin,
  val performancePercent: Double,
  val averagePurchasePrice: Double,
  val ownedAmountInUnit: Double,
  val ownedAmountInFiat: Double,
)
