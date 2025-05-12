package com.multiplatform.coinbo.coins.domain

import com.multiplatform.coinbo.coins.data.remote.mapper.toCoinModel
import com.multiplatform.coinbo.coins.domain.api.CoinsRemoteDataSource
import com.multiplatform.coinbo.coins.domain.model.CoinModel
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.domain.map

class GetCoinsListUseCase(
  private val client: CoinsRemoteDataSource,
) {
  suspend fun execute(): Result<List<CoinModel>, DataError.Remote> {
    return client.getListOfCoins().map { dto ->
      dto.data.coins.map { it.toCoinModel() }
    }
  }
}
