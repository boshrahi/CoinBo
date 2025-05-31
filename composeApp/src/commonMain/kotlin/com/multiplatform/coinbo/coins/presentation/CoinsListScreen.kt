package com.multiplatform.coinbo.coins.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import coinbo.composeapp.generated.resources.close
import com.multiplatform.coinbo.coins.presentation.component.PerformanceChart
import com.multiplatform.coinbo.theme.LocalCoinBoColorsPalette
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CoinsListScreen(
  onCoinClick: (String) -> Unit,
) {
  val viewModel = koinViewModel<CoinsListViewModel>()
  val state by viewModel.state.collectAsStateWithLifecycle()

  CoinsListContent(
    state = state,
    onCoinClick = onCoinClick,
    onDismiss = { viewModel.onDismissChart() },
    onCoinLongClick = { coinId -> viewModel.getCoinPriceList(coinId) },
  )
}

@Composable
fun CoinsListContent(
  state: CoinsState,
  onCoinClick: (String) -> Unit,
  onCoinLongClick: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  Box(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
  ) {
    if (state.chartState != null) {
      CoinChartDialog(
        uiChartState = state.chartState,
        onDismiss = {
          onDismiss()
        },
      )
    }
    CoinsList(
      coins = state.coins,
      onCoinClick = onCoinClick,
      onCoinLongClick = onCoinLongClick,
    )
  }
}

@Composable
fun CoinsList(
  coins: List<UiCoinListItem>,
  onCoinClick: (String) -> Unit,
  onCoinLongClick: (String) -> Unit,
) {
  Box(
    modifier = Modifier.background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      item {
        Text(
          modifier = Modifier.padding(16.dp),
          text = "Top Coins: 🔥?",
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )
      }
      items(coins) { coin ->
        CoinListItem(
          coin = coin,
          onClick = { onCoinClick(it) },
          onCoinLongClick = { onCoinLongClick(it) },
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinListItem(
  coin: UiCoinListItem,
  onClick: (String) -> Unit,
  onCoinLongClick: (String) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .combinedClickable(
        onLongClick = { onCoinLongClick(coin.id) },
        onClick = { onClick(coin.id) },
      )
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AsyncImage(
      modifier = Modifier.padding(end = 4.dp).clip(CircleShape).size(40.dp),
      model = coin.iconUrl,
      contentDescription = null,
      contentScale = ContentScale.Fit,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(
      modifier = Modifier.weight(1f),
    ) {
      Text(
        coin.name,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        coin.symbol,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column(
      horizontalAlignment = Alignment.End,
    ) {
      Text(
        coin.formattedPrice,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        coin.formattedChange,
        color = if (coin.isPositive) {
          LocalCoinBoColorsPalette.current.profitGreen
        } else {
          LocalCoinBoColorsPalette.current.lossRed
        },
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
      )
    }
  }
}

@Composable
fun CoinChartDialog(
  uiChartState: UiChartState,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    modifier = Modifier.fillMaxWidth(),
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "24h Price chart for ${uiChartState.coinName}",
      )
    },
    text = {
      if (uiChartState.isLoading) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator(modifier = Modifier.size(32.dp))
        }
      } else {
        PerformanceChart(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
          nodes = uiChartState.sparkLine,
          profitColor = LocalCoinBoColorsPalette.current.profitGreen,
          lossColor = LocalCoinBoColorsPalette.current.lossRed,
        )
      }
    },
    confirmButton = {},
    dismissButton = {
      Button(
        onClick = onDismiss,
      ) {
        Text(
          text = stringResource(Res.string.close),
        )
      }
    },
  )
}
