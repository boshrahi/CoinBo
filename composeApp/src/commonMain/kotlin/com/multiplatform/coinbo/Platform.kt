package com.multiplatform.coinbo

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
