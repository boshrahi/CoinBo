package com.multiplatform.coinbo.core.domain

/** making sealed interface to restrict the implementation of this interface
 allows us to use exhaustive when statements and conditions at compile time
 ensure we are not missing any cases **/
sealed interface DataError: Error {
  enum class Remote: DataError {
    REQUEST_TIMEOUT,
    TOO_MANY_REQUESTS,
    NO_INTERNET,
    SERVER,
    SERIALIZATION,
    UNKNOWN
  }

  enum class Local: DataError {
    DISK_FULL,
    INSUFFICIENT_FUNDS,
    UNKNOWN
  }
}
