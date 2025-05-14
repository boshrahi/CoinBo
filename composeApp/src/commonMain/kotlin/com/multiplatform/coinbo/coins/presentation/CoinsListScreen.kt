package com.multiplatform.coinbo.coins.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.multiplatform.coinbo.theme.LocalCoinBoColorsPalette
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
  )
}

@Composable
fun CoinsListContent(
  state: CoinsState,
  onCoinClick: (String) -> Unit,
) {
  Box(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
  ) {
    CoinsList(
      coins = state.coins,
      onCoinClick = onCoinClick,
    )
  }
}

@Composable
fun CoinsList(coins: List<UiCoinListItem>, onCoinClick: (String) -> Unit) {
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
        )
      }
    }
  }
}

@Composable
fun CoinListItem(coin: UiCoinListItem, onClick: (String) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(16.dp).clickable {
      onClick(coin.id)
    },
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
