package com.multiplatform.coinbo.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Using Factory pattern so each platform can use it for http client engine
 * */

object HttpClientFactory {
  fun create(engine: HttpClientEngine): HttpClient {
    return HttpClient(engine) {
      install(ContentNegotiation) {
        json(
          json = Json {
            ignoreUnknownKeys = true
          },
        )
      }
      install(HttpTimeout) {
        socketTimeoutMillis = 20_000L
        requestTimeoutMillis = 20_000L
      }
      install(HttpCache)
      defaultRequest {
        headers { append("x-access-token", ApiKeys.COIN_RANKING_API_KEY) }
        contentType(ContentType.Application.Json)
      }
    }
  }
}
