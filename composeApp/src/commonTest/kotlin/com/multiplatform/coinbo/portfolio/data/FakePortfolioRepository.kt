package com.multiplatform.coinbo.portfolio.data

import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.EmptyResult
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.coin.Coin
import com.multiplatform.coinbo.portfolio.domain.PortfolioCoinModel
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePortfolioRepository : PortfolioRepository {

  private val _data = MutableStateFlow<Result<List<PortfolioCoinModel>, DataError.Remote>>(
    Result.Success(emptyList()),
  )

  private val _cashBalance = MutableStateFlow(cashBalance)
  private val _portfolioValue = MutableStateFlow(portfolioValue)

  private val listOfCoins = mutableListOf<PortfolioCoinModel>()

  override suspend fun initializeBalance() {
    // no-op
  }

  override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
    return _data.asStateFlow()
  }

  override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
    return Result.Success(portfolioCoin)
  }

  override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
    listOfCoins.add(portfolioCoin)
    _portfolioValue.value = listOfCoins.sumOf { it.ownedAmountInFiat }
    _data.value = Result.Success(listOfCoins)
    return Result.Success(Unit)
  }

  override suspend fun removeCoinFromPortfolio(coinId: String) {
    _data.update { Result.Success(emptyList()) }
  }

  override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
    return _portfolioValue.map { Result.Success(it) }
  }

  override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
    return _cashBalance.combine(_portfolioValue) { cashBalance, portfolioValue ->
      cashBalance + portfolioValue
    }.map { Result.Success(it) }
  }

  override fun cashBalanceFlow(): Flow<Double> {
    return _cashBalance.asStateFlow()
  }

  override suspend fun updateCashBalance(newBalance: Double) {
    _cashBalance.value = newBalance
  }

  fun simulateError() {
    _data.value = Result.Failure(DataError.Remote.SERVER)
  }

  companion object {
    val fakeCoin = Coin(
      id = "fakeId",
      name = "Fake Coin",
      symbol = "FAKE",
      iconUrl = "https://fake.url/fake.png",
    )
    val portfolioCoin = PortfolioCoinModel(
      coin = fakeCoin,
      ownedAmountInUnit = 1000.0,
      ownedAmountInFiat = 3000.0,
      performancePercent = 10.0,
      averagePurchasePrice = 10.0,
    )
    val cashBalance = 10000.0
    val portfolioValue = 0.0
  }
}
