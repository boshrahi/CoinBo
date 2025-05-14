package com.multiplatform.coinbo.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformModule = module {
  // Define platform-specific dependencies here
  // For example, you can provide iOS-specific implementations of interfaces
  // or any other dependencies that are specific to the iOS platform.
  single<HttpClientEngine> { Darwin.create() }
}
