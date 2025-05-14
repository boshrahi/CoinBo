package com.multiplatform.coinbo

import android.app.Application
import com.multiplatform.coinbo.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class CoinBoApplication : Application(), KoinComponent {

  override fun onCreate() {
    super.onCreate()
    initKoin {
      androidLogger()
      androidContext(this@CoinBoApplication)
    }
  }
}
