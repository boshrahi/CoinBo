package com.multiplatform.coinbo.portfolio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PortfolioCoinEntity(
  @PrimaryKey val coinId: String,
  val name: String,
  val symbol: String,
  val iconUrl: String,
  val averagePurchasePrice: Double, // user may buy coins in different times with different prices
  val amountOwned: Double,
  val timestamp: Long,
)
