package com.multiplatform.coinbo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.multiplatform.coinbo.coins.presentation.CoinsListScreen
import com.multiplatform.coinbo.core.navigation.Buy
import com.multiplatform.coinbo.core.navigation.Coins
import com.multiplatform.coinbo.core.navigation.Portfolio
import com.multiplatform.coinbo.core.navigation.Sell
import com.multiplatform.coinbo.portfolio.presentation.PortfolioScreen
import com.multiplatform.coinbo.theme.CoinBoTheme
import com.multiplatform.coinbo.trade.presentation.buy.BuyScreen
import dev.coinroutine.app.trade.presentation.sell.SellScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  val navController: NavHostController = rememberNavController()
  CoinBoTheme {
    NavHost(
      navController = navController,
      startDestination = Portfolio,
      modifier = Modifier.fillMaxSize(),
    ) {
      composable<Portfolio> {
        PortfolioScreen(
          onCoinItemClicked = { coinId ->
            navController.navigate(Sell(coinId))
          },
          onDiscoverCoinsClicked = {
            navController.navigate(Coins)
          },
        )
      }

      composable<Coins> {
        CoinsListScreen { coinId ->
          navController.navigate(Buy(coinId))
        }
      }

      composable<Buy> { navBackStackEntry ->
        val coinId: String = navBackStackEntry.toRoute<Buy>().coinId
        BuyScreen(
          coinId = coinId,
          navigateToPortfolio = {
            navController.navigate(Portfolio) {
              popUpTo(Portfolio) { inclusive = true }
            }
          },
        )
      }
      composable<Sell> { navBackStackEntry ->
        val coinId: String = navBackStackEntry.toRoute<Sell>().coinId
        SellScreen(
          coinId = coinId,
          navigateToPortfolio = {
            navController.navigate(Portfolio) {
              popUpTo(Portfolio) { inclusive = true }
            }
          },
        )
      }
    }
  }
}
