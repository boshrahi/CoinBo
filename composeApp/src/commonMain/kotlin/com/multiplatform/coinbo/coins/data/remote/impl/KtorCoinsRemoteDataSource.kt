package com.multiplatform.coinbo.coins.data.remote.impl

import com.multiplatform.coinbo.coins.data.remote.dto.CoinDetailsResponseDto
import com.multiplatform.coinbo.coins.data.remote.dto.CoinPriceHistoryResponseDto
import com.multiplatform.coinbo.coins.data.remote.dto.CoinsResponseDto
import com.multiplatform.coinbo.coins.domain.api.CoinsRemoteDataSource
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "https://api.coinranking.com/v2"

class KtorCoinsRemoteDataSource(
  private val httpClient: HttpClient,
) : CoinsRemoteDataSource {

  override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
    return safeCall {
      httpClient.get("$BASE_URL/coins")
    }
  }

  override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
    return safeCall {
      httpClient.get("$BASE_URL/coin/$coinId/history")
    }
  }

  override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
    return safeCall {
      httpClient.get("$BASE_URL/coin/$coinId")
    }
  }
}
