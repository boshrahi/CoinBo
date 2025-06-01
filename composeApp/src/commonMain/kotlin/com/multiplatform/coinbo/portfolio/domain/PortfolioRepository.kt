package com.multiplatform.coinbo.portfolio.domain

import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.EmptyResult
import com.multiplatform.coinbo.core.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * domain layer abstract logic of data layer
 * make it easier to switch to local database or remote data source
 * it is reactive and returns Flow<Result<>> to observe changes,
 * immediately changes are available in UI layer
 * proper error handling
 * */
interface PortfolioRepository {

  suspend fun initializeBalance()
  fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>>
  suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote>
  suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local>
  suspend fun removeCoinFromPortfolio(coinId: String)

  fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>>
  fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>>
  fun cashBalanceFlow(): Flow<Double>
  suspend fun updateCashBalance(newBalance: Double)
}
