package com.multiplatform.coinbo.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.multiplatform.coinbo.core.database.portfolio.PortfolioDatabase
import platform.Foundation.NSHomeDirectory

fun getPortfolioDatabaseBuilder(): RoomDatabase.Builder<PortfolioDatabase> {
  // iOS does not have a context like Android, so we use NSHomeDirectory to get the path
  //
  val dbFile = NSHomeDirectory() + "/portfolio.db"
  return Room.databaseBuilder<PortfolioDatabase>(
    name = dbFile,
  )
}
