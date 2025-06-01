package com.multiplatform.coinbo.core.database.portfolio

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * we need to handle the database creation in a platform-specific way
 * RoomDatabaseConstructor is an interface that allows us to create a RoomDatabase
 * abstract creation of db for each platform
 * that's why we add Suppress("NO_ACTUAL_FOR_EXPECT")
 * */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PortfolioDatabaseCreator : RoomDatabaseConstructor<PortfolioDatabase>

fun getPortfolioDatabase(
  builder: RoomDatabase.Builder<PortfolioDatabase>,
): PortfolioDatabase {
  return builder
    // .addMigrations(MIGRATIONS)
    // .fallbackToDestructiveMigrationOnDowngrade()
    .setDriver(BundledSQLiteDriver()) // is and aspect of the RoomDatabaseConstructor previously
    // we had to have different drivers for each platform
    .setQueryCoroutineContext(Dispatchers.IO) // this is the context for the queries, we use IO for database operations
    .build()
}
