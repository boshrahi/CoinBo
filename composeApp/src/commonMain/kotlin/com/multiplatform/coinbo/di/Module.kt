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
}
