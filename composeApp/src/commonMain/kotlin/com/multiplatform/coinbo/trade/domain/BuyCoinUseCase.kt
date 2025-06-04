package com.multiplatform.coinbo.trade.domain

import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.EmptyResult
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.coin.Coin
import com.multiplatform.coinbo.portfolio.domain.PortfolioCoinModel
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.first

class BuyCoinUseCase(
  private val portfolioRepository: PortfolioRepository,
) {
  suspend fun buyCoin(
    coin: Coin,
    amountInFiat: Double,
    price: Double,
  ): EmptyResult<DataError> {
    val balance = portfolioRepository.cashBalanceFlow().first()
    if (balance < amountInFiat) {
      return Result.Failure(DataError.Local.INSUFFICIENT_FUNDS)
    }

    val existingCoinResult = portfolioRepository.getPortfolioCoin(coin.id)
    val existingCoin = when (existingCoinResult) {
      is Result.Success -> existingCoinResult.data
      is Result.Failure -> return Result.Failure(existingCoinResult.error)
    }
    val amountInUnit = amountInFiat / price
    if (existingCoin != null) {
      val newAmountOwned = existingCoin.ownedAmountInUnit + amountInUnit
      val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInFiat
      val newAveragePurchasePrice = newTotalInvestment / newAmountOwned
      portfolioRepository.savePortfolioCoin(
        existingCoin.copy(
          ownedAmountInUnit = newAmountOwned,
          ownedAmountInFiat = newTotalInvestment,
          averagePurchasePrice = newAveragePurchasePrice,
        ),
      )
    } else {
      portfolioRepository.savePortfolioCoin(
        PortfolioCoinModel(
          coin = coin,
          performancePercent = 0.0,
          averagePurchasePrice = price,
          ownedAmountInFiat = amountInFiat,
          ownedAmountInUnit = amountInUnit,
        ),
      )
    }
    portfolioRepository.updateCashBalance(balance - amountInFiat)
    return Result.Success(Unit)
  }
}
