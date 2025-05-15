package com.multiplatform.coinbo.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiplatform.coinbo.coins.domain.GetCoinsListUseCase
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.util.formatFiat
import com.multiplatform.coinbo.core.util.formatPercentages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import toUiText

/**
 * viewModel class supports both android and ios (thank God!)
 * onStart is to call getAllCoins() when someone starts collecting
 * better than calling on init block for testability
 * */
class CoinsListViewModel(
  private val getCoinsListUseCase: GetCoinsListUseCase,
) : ViewModel() {

  private val _state = MutableStateFlow(CoinsState())
  val state = _state.onStart {
    getAllCoins()
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = CoinsState(),
  )

  private suspend fun getAllCoins() {
    when (val coinsRes = getCoinsListUseCase.execute()) {
      is Result.Success -> {
        _state.update {
          CoinsState(
            coins = coinsRes.data.map { coin ->
              UiCoinListItem(
                id = coin.coin.id,
                name = coin.coin.name,
                iconUrl = coin.coin.iconUrl,
                symbol = coin.coin.symbol,
                formattedChange = formatPercentages(coin.change),
                formattedPrice = formatFiat(coin.price),
                isPositive = coin.change >= 0,
              )
            },
          )
        }
      }
      is Result.Failure -> {
        _state.update {
          it.copy(
            coins = emptyList(),
            error = coinsRes.error.toUiText(),
          )
        }
      }
    }
  }
}
