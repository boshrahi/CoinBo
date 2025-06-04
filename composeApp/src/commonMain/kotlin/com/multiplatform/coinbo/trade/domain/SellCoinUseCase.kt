package com.multiplatform.coinbo.trade.domain

import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.EmptyResult
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.coin.Coin
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.first

class SellCoinUseCase(
  private val portfolioRepository: PortfolioRepository,
) {
  suspend fun sellCoin(
    coin: Coin,
    amountInFiat: Double,
    price: Double,
  ): EmptyResult<DataError> {
    /**
     * if the remaining asset worth less than $1.
     * We sell the entire remaining amount to avoid leaving behind any dust value in the portfolio.
     * Dust means any asset that was less than $1.
     * So because we are only supporting sell and buy in fiat, not in the asset unit,
     * it might happen because of the difference in prices.
     * for example I have Cardano and it was $24.87.
     * And if I sell a $24 dollar it would sell all of the Cardano .
     * */
    val sellAllThreshold = 1
    when (val existingCoinResponse = portfolioRepository.getPortfolioCoin(coin.id)) {
      is Result.Success -> {
        val existingCoin = existingCoinResponse.data
        val sellAmountInUnit = amountInFiat / price

        val balance = portfolioRepository.cashBalanceFlow().first()
        if (existingCoin == null || existingCoin.ownedAmountInUnit < sellAmountInUnit) {
          return Result.Failure(DataError.Local.INSUFFICIENT_FUNDS)
        }
        val remainingAmountFiat = existingCoin.ownedAmountInFiat - amountInFiat
        val remainingAmountUnit = existingCoin.ownedAmountInUnit - sellAmountInUnit
        if (remainingAmountFiat < sellAllThreshold) {
          portfolioRepository.removeCoinFromPortfolio(coin.id)
        } else {
          portfolioRepository.savePortfolioCoin(
            existingCoin.copy(
              ownedAmountInUnit = remainingAmountUnit,
              ownedAmountInFiat = remainingAmountFiat,
            ),
          )
        }
        portfolioRepository.updateCashBalance(balance + amountInFiat)
        return Result.Success(Unit)
      }
      is Result.Failure -> {
        return existingCoinResponse
      }
    }
  }
}
