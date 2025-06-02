package com.multiplatform.coinbo.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coinbo.composeapp.generated.resources.Res
import coinbo.composeapp.generated.resources.buy_coin_title
import coinbo.composeapp.generated.resources.cash_balance_title
import coinbo.composeapp.generated.resources.discover_coins_title
import coinbo.composeapp.generated.resources.no_coin_error
import coinbo.composeapp.generated.resources.owned_coin_title
import coinbo.composeapp.generated.resources.total_value_title
import com.multiplatform.coinbo.theme.LocalCoinBoColorsPalette
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
  onCoinItemClicked: (String) -> Unit,
  onDiscoverCoinsClicked: () -> Unit,
) {
  val portfolioViewModel = koinViewModel<PortfolioViewModel>()
  // kotlin by delegation, ensures ui state updates automatically synced with lifecycle
  val state by portfolioViewModel.state.collectAsStateWithLifecycle()
  if (state.isLoading) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize(),
    ) {
      CircularProgressIndicator(
        color = LocalCoinBoColorsPalette.current.profitGreen,
        modifier = Modifier.size(32.dp),
      )
    }
  } else {
    PortfolioContent(
      state = state,
      onCoinItemClicked = onCoinItemClicked,
      onDiscoverCoinsClicked = onDiscoverCoinsClicked,
    )
  }
}

@Composable
fun PortfolioContent(
  state: PortfolioState,
  onCoinItemClicked: (String) -> Unit,
  onDiscoverCoinsClicked: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.inversePrimary),
  ) {
    PortfolioBalanceSection(
      portfolioValue = state.portfolioValue,
      cashBalance = state.cashBalance,
      showBuyButton = state.showBuyButton,
      onBuyButtonClicked = onDiscoverCoinsClicked,
    )
    PortfolioCoinsList(
      coins = state.coins,
      onCoinItemClicked = onCoinItemClicked,
      onDiscoverCoinsClicked = onDiscoverCoinsClicked,
    )
  }
}

@Composable
private fun PortfolioBalanceSection(
  portfolioValue: String,
  cashBalance: String,
  showBuyButton: Boolean,
  onBuyButtonClicked: () -> Unit,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxHeight(0.3f) // fill 30% of the screen height
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.inversePrimary)
      .padding(32.dp),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.weight(1f))
      Text(
        text = stringResource(Res.string.total_value_title),
        color = MaterialTheme.colorScheme.primary,
        fontSize = MaterialTheme.typography.titleSmall.fontSize,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = portfolioValue,
        color = MaterialTheme.colorScheme.primary,
        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
      )
      Row {
        Text(
          text = stringResource(Res.string.cash_balance_title),
          color = MaterialTheme.colorScheme.primary,
          fontSize = MaterialTheme.typography.bodySmall.fontSize,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = cashBalance,
          color = MaterialTheme.colorScheme.primary,
          fontSize = MaterialTheme.typography.bodySmall.fontSize,
        )
      }
      if (showBuyButton) {
        Spacer(modifier = Modifier.weight(1f))
        Button(
          onClick = onBuyButtonClicked,
          colors = ButtonDefaults.buttonColors(
            containerColor = LocalCoinBoColorsPalette.current.profitGreen,
          ),
          contentPadding = PaddingValues(horizontal = 64.dp),
        ) {
          Text(
            text = stringResource(Res.string.buy_coin_title),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun PortfolioCoinsList(
  coins: List<UiPortfolioCoinItem>,
  onCoinItemClicked: (String) -> Unit,
  onDiscoverCoinsClicked: () -> Unit,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxHeight()
      .fillMaxWidth()
      .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
      .background(MaterialTheme.colorScheme.background),
  ) {
    if (coins.isEmpty()) {
      PortfolioEmptySection(
        onDiscoverCoinsClicked = onDiscoverCoinsClicked,
      )
      return@Box
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize(),
      ) {
        Text(
          text = stringResource(Res.string.owned_coin_title),
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = MaterialTheme.typography.titleLarge.fontSize,
          modifier = Modifier
            .padding(16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(coins) { coin ->
            CoinListItem(
              coin = coin,
              onCoinItemClicked = onCoinItemClicked,
            )
          }
        }
      }
    }
  }
}

@Composable
fun CoinListItem(
  coin: UiPortfolioCoinItem,
  onCoinItemClicked: (String) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        onCoinItemClicked.invoke(coin.id)
      }
      .padding(16.dp),
  ) {
    AsyncImage(
      model = coin.iconUrl,
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier.padding(4.dp).clip(CircleShape).size(40.dp),
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(
      modifier = Modifier.weight(1f),
    ) {
      Text(
        text = coin.name,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = coin.amountInUnitText,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.typography.titleSmall.fontSize,
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = coin.amountInFiatText,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = coin.performancePercentText,
        color = if (coin.isPositive) LocalCoinBoColorsPalette.current.profitGreen else LocalCoinBoColorsPalette.current.lossRed,
        fontSize = MaterialTheme.typography.titleSmall.fontSize,
      )
    }
  }
}

@Composable
fun PortfolioEmptySection(
  onDiscoverCoinsClicked: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .fillMaxHeight()
      .fillMaxWidth()
      .padding(32.dp),
  ) {
    Text(
      text = stringResource(Res.string.no_coin_error),
      color = MaterialTheme.colorScheme.primary,
      fontSize = MaterialTheme.typography.titleSmall.fontSize,
    )
    Button(
      onClick = onDiscoverCoinsClicked,
      colors = ButtonDefaults.buttonColors(
        containerColor = LocalCoinBoColorsPalette.current.profitGreen,
      ),
      contentPadding = PaddingValues(horizontal = 64.dp),
    ) {
      Text(
        text = stringResource(Res.string.discover_coins_title),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimary,
      )
    }
  }
}
