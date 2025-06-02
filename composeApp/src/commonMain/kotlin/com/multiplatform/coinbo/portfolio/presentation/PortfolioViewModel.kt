package com.multiplatform.coinbo.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result
import com.multiplatform.coinbo.core.util.formatCoinUnit
import com.multiplatform.coinbo.core.util.formatFiat
import com.multiplatform.coinbo.core.util.formatPercentages
import com.multiplatform.coinbo.portfolio.domain.PortfolioCoinModel
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import toUiText

class PortfolioViewModel(
  private val portfolioRepository: PortfolioRepository,
  coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

  private val _state = MutableStateFlow(PortfolioState(isLoading = true))
  val state: StateFlow<PortfolioState> = combine(
    _state,
    portfolioRepository.allPortfolioCoinsFlow(),
    portfolioRepository.totalBalanceFlow(),
    portfolioRepository.cashBalanceFlow(),
  ) { currentState, portfolioCoinsResponse, totalBalanceResult, cashBalance ->
    when (portfolioCoinsResponse) {
      is Result.Success -> {
        handleSuccessState(
          currentState = currentState,
          portfolioCoins = portfolioCoinsResponse.data,
          totalBalanceResult = totalBalanceResult,
          cashBalance = cashBalance,
        )
      }

      is Result.Failure -> {
        handleErrorState(
          currentState = currentState,
          portfolioCoinsResponse.error,
        )
      }
    }
  }.onStart {
    portfolioRepository.initializeBalance()
  }.flowOn(coroutineDispatcher).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = PortfolioState(isLoading = true),
  )

  private fun handleSuccessState(
    currentState: PortfolioState,
    portfolioCoins: List<PortfolioCoinModel>,
    totalBalanceResult: Result<Double, DataError>,
    cashBalance: Double,
  ): PortfolioState {
    val portfolioValue = when (totalBalanceResult) {
      is Result.Success -> formatFiat(totalBalanceResult.data)
      is Result.Failure -> formatFiat(0.0)
    }

    return currentState.copy(
      coins = portfolioCoins.map { it.toUiPortfolioCoinItem() },
      portfolioValue = portfolioValue,
      cashBalance = formatFiat(cashBalance),
      showBuyButton = portfolioCoins.isNotEmpty(),
      isLoading = false,
    )
  }

  private fun handleErrorState(
    currentState: PortfolioState,
    error: DataError,
  ): PortfolioState {
    return currentState.copy(
      isLoading = false,
      error = error.toUiText(),
    )
  }

  private fun PortfolioCoinModel.toUiPortfolioCoinItem(): UiPortfolioCoinItem {
    return UiPortfolioCoinItem(
      id = coin.id,
      name = coin.name,
      iconUrl = coin.iconUrl,
      amountInUnitText = formatCoinUnit(ownedAmountInUnit, coin.symbol),
      amountInFiatText = formatFiat(ownedAmountInFiat),
      performancePercentText = formatPercentages(performancePercent),
      isPositive = performancePercent >= 0,
    )
  }
}
