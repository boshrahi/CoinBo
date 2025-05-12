package com.multiplatform.coinbo.coins.data.remote.mapper

import com.multiplatform.coinbo.coins.data.remote.dto.CoinItemDto
import com.multiplatform.coinbo.coins.data.remote.dto.CoinPriceDto
import com.multiplatform.coinbo.coins.domain.model.CoinModel
import com.multiplatform.coinbo.coins.domain.model.PriceModel
import com.multiplatform.coinbo.core.domain.coin.Coin

fun CoinItemDto.toCoinModel() = CoinModel(
  coin = Coin(
    id = uuid,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
  ),
  price = price,
  change = change,
)
fun CoinPriceDto.toPriceModel() = PriceModel(
  price = price ?: 0.0,
  timestamp = timestamp,
)
