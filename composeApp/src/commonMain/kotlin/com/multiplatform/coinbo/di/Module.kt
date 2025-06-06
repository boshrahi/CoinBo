package com.multiplatform.coinbo.di

import androidx.room.RoomDatabase
import com.multiplatform.coinbo.coins.data.remote.impl.KtorCoinsRemoteDataSource
import com.multiplatform.coinbo.coins.domain.GetCoinDetailsUseCase
import com.multiplatform.coinbo.coins.domain.GetCoinPriceHistoryUseCase
import com.multiplatform.coinbo.coins.domain.GetCoinsListUseCase
import com.multiplatform.coinbo.coins.domain.api.CoinsRemoteDataSource
import com.multiplatform.coinbo.coins.presentation.CoinsListViewModel
import com.multiplatform.coinbo.core.database.portfolio.PortfolioDatabase
import com.multiplatform.coinbo.core.database.portfolio.getPortfolioDatabase
import com.multiplatform.coinbo.core.network.HttpClientFactory
import com.multiplatform.coinbo.portfolio.data.PortfolioRepositoryImpl
import com.multiplatform.coinbo.portfolio.domain.PortfolioRepository
import com.multiplatform.coinbo.portfolio.presentation.PortfolioViewModel
import com.multiplatform.coinbo.trade.domain.BuyCoinUseCase
import com.multiplatform.coinbo.trade.domain.SellCoinUseCase
import com.multiplatform.coinbo.trade.presentation.buy.BuyViewModel
import com.multiplatform.coinbo.trade.presentation.sell.SellViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Create top level function to init koin follow documentation
 * pass additional configuration
 * we startKoin and pass our modules to it
 * expect keyword is used to declare a common API
 * actual keyword is used to implement the common API in iOS and Android
 * + platform module
 * + shared module
 * */
fun initKoin(config: KoinAppDeclaration? = null) =
  startKoin {
    config?.invoke(this)
    modules(
      listOf(
        platformModule,
        sharedModule,
      ),
    )
  }

expect val platformModule: Module
val sharedModule = module {
  // core
  single<HttpClient> { HttpClientFactory.create(get()) }

  // portfolio
  single {
    getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
  }
  singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
  single { get<PortfolioDatabase>().portfolioDao() }
  single { get<PortfolioDatabase>().userBalanceDao() }
  viewModel { PortfolioViewModel(get()) }

  // coin list
  singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
  singleOf(::GetCoinsListUseCase)
  singleOf(::GetCoinDetailsUseCase)
  singleOf(::GetCoinPriceHistoryUseCase)
  viewModel {
    CoinsListViewModel(
      getCoinsListUseCase = get(),
      getCoinPriceHistoryUseCase = get(),
    )
  }

  // trade
  singleOf(::BuyCoinUseCase)
  singleOf(::SellCoinUseCase)
  viewModel { (coinId: String) -> BuyViewModel(get(), get(), get(), coinId) }
  viewModel { (coinId: String) -> SellViewModel(get(), get(), get(), coinId) }
}
