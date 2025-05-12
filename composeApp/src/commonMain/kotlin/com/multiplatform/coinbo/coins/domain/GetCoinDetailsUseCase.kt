package com.multiplatform.coinbo.coins.domain

import com.multiplatform.coinbo.coins.data.remote.mapper.toCoinModel
import com.multiplatform.coinbo.coins.domain.api.CoinsRemoteDataSource
import com.multiplatform.coinbo.coins.domain.model.CoinModel
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.map

class GetCoinDetailsUseCase(
  private val client: CoinsRemoteDataSource,
) {
  suspend fun execute(coinId: String): Result<CoinModel, DataError.Remote> {
    return client.getCoinById(coinId).map { dto ->
      dto.data.coin.toCoinModel()
    }
  }
}
