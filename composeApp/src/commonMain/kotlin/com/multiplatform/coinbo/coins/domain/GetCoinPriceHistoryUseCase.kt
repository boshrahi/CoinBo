package com.multiplatform.coinbo.coins.domain

import com.multiplatform.coinbo.coins.data.remote.mapper.toPriceModel
import com.multiplatform.coinbo.coins.domain.api.CoinsRemoteDataSource
import com.multiplatform.coinbo.coins.domain.model.PriceModel
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.map

class GetCoinPriceHistoryUseCase(
  private val client: CoinsRemoteDataSource,
) {
  suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
    return client.getPriceHistory(coinId).map { dto ->
      dto.data.history.map { it.toPriceModel() }
    }
  }
}
