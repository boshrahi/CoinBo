package com.multiplatform.coinbo.coins.domain.api

import com.multiplatform.coinbo.coins.data.remote.dto.CoinDetailsResponseDto
import com.multiplatform.coinbo.coins.data.remote.dto.CoinPriceHistoryResponseDto
import com.multiplatform.coinbo.coins.data.remote.dto.CoinsResponseDto
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result

interface CoinsRemoteDataSource {

  suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>

  suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>

  suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}
