package com.multiplatform.coinbo.trade.presentation.mapper

import com.multiplatform.coinbo.core.domain.coin.Coin
import com.multiplatform.coinbo.trade.presentation.common.UiTradeCoinItem

fun UiTradeCoinItem.toCoin() = Coin(
  id = id,
  name = name,
  symbol = symbol,
  iconUrl = iconUrl,
)
