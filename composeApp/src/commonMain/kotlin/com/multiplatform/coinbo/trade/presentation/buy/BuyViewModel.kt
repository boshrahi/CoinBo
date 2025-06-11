package com.multiplatform.coinbo.trade.presentation.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiplatform.coinbo.coins.domain.GetCoinDetailsUseCase
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.util.formatFiat
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import com.multiplatform.coinbo.trade.domain.BuyCoinUseCase
import com.multiplatform.coinbo.trade.presentation.common.TradeState
import com.multiplatform.coinbo.trade.presentation.common.UiTradeCoinItem
import com.multiplatform.coinbo.trade.presentation.mapper.toCoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import toUiText

class BuyViewModel(
  private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
  private val portfolioRepository: PortfolioRepository,
  private val buyCoinUseCase: BuyCoinUseCase,
  private val coinId: String,
) : ViewModel() {

  private val _amount = MutableStateFlow("")
  private val _state = MutableStateFlow(TradeState())
  val state = combine(
    _state,
    _amount,
  ) { state, amount ->
    state.copy(
      amount = amount,
    )
  }.onStart {
    val balance = portfolioRepository.cashBalanceFlow().first()
    getCoinDetails(balance)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = TradeState(isLoading = true),
  )

  /**
   * a channel in Kotlin is a coroutine based construct
   * for asynchronous message passing.
   * It allows us to send discrete events that can be buffered and processed sequentially.
   * Unlike a stateflow, which always holds the latest value and emits the state changes.
   * A channel is perfect for one off events where you might want to buffer multiple items
   * before they are collected.
   * */
  private val _events = Channel<BuyEvents>(capacity = Channel.BUFFERED)
  val events = _events.receiveAsFlow()

  private suspend fun getCoinDetails(balance: Double) {
    when (val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
      is Result.Success -> {
        _state.update {
          it.copy(
            coin = UiTradeCoinItem(
              id = coinResponse.data.coin.id,
              name = coinResponse.data.coin.name,
              symbol = coinResponse.data.coin.symbol,
              iconUrl = coinResponse.data.coin.iconUrl,
              price = coinResponse.data.price,
            ),
            availableAmount = "Available: ${formatFiat(balance)}",
          )
        }
      }

      is Result.Failure -> {
        _state.update {
          it.copy(
            isLoading = false,
            error = coinResponse.error.toUiText(),
          )
        }
      }
    }
  }

  fun onAmountChanged(amount: String) {
    _amount.value = amount
  }

  fun onBuyClicked() {
    val tradeCoin = state.value.coin ?: return
    viewModelScope.launch {
      val buyCoinResponse = buyCoinUseCase.buyCoin(
        coin = tradeCoin.toCoin(),
        amountInFiat = _amount.value.toDouble(),
        price = tradeCoin.price,
      )

      when (buyCoinResponse) {
        is Result.Success -> {
          _events.send(BuyEvents.BuySuccess)
        }
        is Result.Failure -> {
          _state.update {
            it.copy(
              isLoading = false,
              error = buyCoinResponse.error.toUiText(),
            )
          }
        }
      }
    }
  }
}

sealed interface BuyEvents {
  data object BuySuccess : BuyEvents
}
