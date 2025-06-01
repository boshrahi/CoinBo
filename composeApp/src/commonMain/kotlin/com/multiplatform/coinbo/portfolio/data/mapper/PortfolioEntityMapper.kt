package com.multiplatform.coinbo.portfolio.data.mapper

import com.multiplatform.coinbo.core.domain.coin.Coin
import com.multiplatform.coinbo.portfolio.data.local.PortfolioCoinEntity
import com.multiplatform.coinbo.portfolio.domain.PortfolioCoinModel
import kotlinx.datetime.Clock

fun PortfolioCoinEntity.toPortfolioCoinModel(
  currentPrice: Double,
): PortfolioCoinModel {
  return PortfolioCoinModel(
    coin = Coin(
      id = coinId,
      name = name,
      symbol = symbol,
      iconUrl = iconUrl,
    ),
    performancePercent = ((currentPrice - averagePurchasePrice) / averagePurchasePrice) * 100,
    averagePurchasePrice = averagePurchasePrice,
    ownedAmountInUnit = amountOwned,
    ownedAmountInFiat = amountOwned * currentPrice,
  )
}

fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity {
  return PortfolioCoinEntity(
    coinId = coin.id,
    name = coin.name,
    symbol = coin.symbol,
    iconUrl = coin.iconUrl,
    amountOwned = ownedAmountInUnit,
    averagePurchasePrice = averagePurchasePrice,
    // we cannot use System.currentTimeMillis() here because it is java specific
    // kotlin data time library is used for multiplatform compatibility
    timestamp = Clock.System.now().toEpochMilliseconds(),
  )
}
