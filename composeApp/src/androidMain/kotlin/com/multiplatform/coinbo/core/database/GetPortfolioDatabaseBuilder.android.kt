package com.multiplatform.coinbo.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.multiplatform.coinbo.core.database.portfolio.PortfolioDatabase

fun getPortfolioDatabaseBuilder(context: Context): RoomDatabase.Builder<PortfolioDatabase> {
  val dbFile = context.getDatabasePath("portfolio.db")
  return Room.databaseBuilder<PortfolioDatabase>(
    context = context,
    name = dbFile.absolutePath,
  )
}
