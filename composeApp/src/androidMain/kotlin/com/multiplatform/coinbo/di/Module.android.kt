package com.multiplatform.coinbo.di

import androidx.room.RoomDatabase
import com.multiplatform.coinbo.core.database.getPortfolioDatabaseBuilder
import com.multiplatform.coinbo.core.database.portfolio.PortfolioDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
  // Define platform-specific dependencies here
  // For example, you can provide Android-specific implementations of interfaces
  // or any other dependencies that are specific to the Android platform.

  single<HttpClientEngine> { Android.create() }
  singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
}
