package com.example.growwassignment.di

import android.app.Application
import androidx.room.Room
import com.example.growwassignment.data.local.WatchlistDatabase
import com.example.growwassignment.data.local.dao.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWatchlistDatabase(app: Application): WatchlistDatabase {
        return Room.databaseBuilder(
            app,
            WatchlistDatabase::class.java,
            WatchlistDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWatchlistDao(db: WatchlistDatabase): WatchlistDao {
        return db.watchlistDao
    }
}