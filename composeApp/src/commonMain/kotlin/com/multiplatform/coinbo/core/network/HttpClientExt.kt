package com.multiplatform.coinbo.core.network

import com.multiplatform.coinbo.core.domain.DataError
import com.multiplatform.coinbo.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
/**
 * These two methods, safeCall and responseToResult, are designed to handle
 * HTTP requests and responses in a safe and structured way using Ktor client in Kotlin.
 * This function wraps a block of code that makes an HTTP request,
 * handles exceptions that might arise during the request,
 * and returns a Result<T, DataError.Remote> indicating either success or failure.
 *
 * + inline: This allows the compiler to inline the function and optimize it (e.g., when used with lambdas).
 * + reified: This keyword is used to preserve the type of T at runtime,
 *   allowing you to call response.body<T>() and have it correctly infer the type.
 *   (otherwise JVM does not retain generic type information)
 *
 * + The coroutineContext.ensureActive() function in Kotlin is used to check whether the coroutine
 *   is still active and throw an exception if the coroutine has been cancelled.
 * **/
suspend inline fun <reified T> safeCall(
  execute: () -> HttpResponse,
): Result<T, DataError.Remote> {
  val response = try {
    execute()
  } catch (e: SocketTimeoutException) {
    return Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
  } catch (e: UnresolvedAddressException) {
    return Result.Failure(DataError.Remote.NO_INTERNET)
  } catch (e: Exception) {
    coroutineContext.ensureActive()
    return Result.Failure(DataError.Remote.UNKNOWN)
  }

  return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
  response: HttpResponse,
): Result<T, DataError.Remote> {
  return when (response.status.value) {
    in 200..299 -> {
      try {
        Result.Success(response.body<T>())
      } catch (e: Exception) {
        Result.Failure(DataError.Remote.SERIALIZATION)
      }
    }
    408 -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    429 -> Result.Failure(DataError.Remote.TOO_MANY_REQUESTS)
    in 500..599 -> Result.Failure(DataError.Remote.SERVER)
    else -> Result.Failure(DataError.Remote.UNKNOWN)
  }
}
