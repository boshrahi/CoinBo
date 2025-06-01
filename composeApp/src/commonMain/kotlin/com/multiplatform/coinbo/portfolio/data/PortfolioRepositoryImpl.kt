package com.multiplatform.coinbo.portfolio.data

import androidx.sqlite.SQLiteException
import com.multiplatform.coinbo.coins.domain.api.CoinsRemoteDataSource
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.EmptyResult
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.onError
import com.multiplatform.coinbo.core.domain.onSuccess
import com.multiplatform.coinbo.portfolio.data.local.PortfolioDao
import com.multiplatform.coinbo.portfolio.data.local.UserBalanceDao
import com.multiplatform.coinbo.portfolio.data.local.UserBalanceEntity
import com.multiplatform.coinbo.portfolio.data.mapper.toPortfolioCoinEntity
import com.multiplatform.coinbo.portfolio.data.mapper.toPortfolioCoinModel
import com.multiplatform.coinbo.portfolio.domain.PortfolioCoinModel
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class PortfolioRepositoryImpl(
  private val portfolioDao: PortfolioDao,
  private val userBalanceDao: UserBalanceDao,
  private val coinsRemoteDataSource: CoinsRemoteDataSource,
) : PortfolioRepository {

  override suspend fun initializeBalance() {
    val currentBalance = userBalanceDao.getCashBalance()
    if (currentBalance == null) {
      userBalanceDao.insertBalance(
        UserBalanceEntity(
          cashBalance = 10000.0,
        ),
      )
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
    return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
      if (portfolioCoinEntities.isEmpty()) {
        flow {
          emit(Result.Success(emptyList<PortfolioCoinModel>()))
        }
      } else {
        flow {
          coinsRemoteDataSource.getListOfCoins()
            .onSuccess { coinsDto ->
              val portfolioCoins =
                portfolioCoinEntities.mapNotNull { portfolioCoinEntity ->
                  val coin = coinsDto.data.coins.find { it.uuid == portfolioCoinEntity.coinId }
                  coin?.let {
                    portfolioCoinEntity.toPortfolioCoinModel(it.price)
                  }
                }
              emit(Result.Success(portfolioCoins))
            }
            .onError { error ->
              emit(Result.Failure(error))
            }
        }
      }
    }.catch {
      emit(Result.Failure(DataError.Remote.UNKNOWN))
    }
  }

  override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
    coinsRemoteDataSource.getCoinById(coinId)
      .onError { error ->
        return Result.Failure(error)
      }
      .onSuccess { coinDto ->
        val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
        return if (portfolioCoinEntity != null) {
          Result.Success(portfolioCoinEntity.toPortfolioCoinModel(coinDto.data.coin.price))
        } else {
          Result.Success(null)
        }
      }
    return Result.Failure(DataError.Remote.UNKNOWN)
  }

  override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
    try {
      portfolioDao.insert(portfolioCoin.toPortfolioCoinEntity())
      return Result.Success(Unit)
    } catch (e: SQLiteException) {
      return Result.Failure(DataError.Local.DISK_FULL)
    }
  }

  override suspend fun removeCoinFromPortfolio(coinId: String) {
    portfolioDao.deletePortfolioItem(coinId)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
    return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinsEntities ->
      if (portfolioCoinsEntities.isEmpty()) {
        flow {
          emit(Result.Success(0.0))
        }
      } else {
        flow {
          val apiResult = coinsRemoteDataSource.getListOfCoins()
          apiResult.onError { error ->
            emit(Result.Failure(error))
          }.onSuccess { coinsDto ->
            val totalValue = portfolioCoinsEntities.sumOf { ownedCoin ->
              val coinPrice = coinsDto.data.coins.find { it.uuid == ownedCoin.coinId }?.price ?: 0.0
              ownedCoin.amountOwned * coinPrice
            }
            emit(Result.Success(totalValue))
          }
        }
      }
    }.catch {
      emit(Result.Failure(DataError.Remote.UNKNOWN))
    }
  }

  override fun cashBalanceFlow(): Flow<Double> {
    return flow {
      emit(userBalanceDao.getCashBalance() ?: 10000.0)
    }
  }

  override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
    return combine(
      cashBalanceFlow(),
      calculateTotalPortfolioValue(),
    ) { cashBalance, portfolioResult ->
      when (portfolioResult) {
        is Result.Success -> {
          Result.Success(cashBalance + portfolioResult.data)
        }

        is Result.Failure -> {
          Result.Failure(portfolioResult.error)
        }
      }
    }
  }

  override suspend fun updateCashBalance(newBalance: Double) {
    userBalanceDao.updateCashBalance(newBalance)
  }
}
