package com.multiplatform.coinbo.portfolio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserBalanceEntity(
  @PrimaryKey val id: Int = 1, // ensure only one record exists for user balance
  val cashBalance: Double,
)
