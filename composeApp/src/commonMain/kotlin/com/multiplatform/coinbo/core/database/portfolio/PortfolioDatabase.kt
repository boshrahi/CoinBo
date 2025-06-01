package com.multiplatform.coinbo.core.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.multiplatform.coinbo.portfolio.data.local.PortfolioCoinEntity
import com.multiplatform.coinbo.portfolio.data.local.PortfolioDao

@ConstructedBy(PortfolioDatabaseCreator::class)
@Database(entities = [PortfolioCoinEntity::class], version = 1)
abstract class PortfolioDatabase : RoomDatabase() {
  abstract fun portfolioDao(): PortfolioDao
}
