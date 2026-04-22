package com.example.growwassignment.di

import com.example.growwassignment.data.local.dao.WatchlistDao
import com.example.growwassignment.data.remote.MfApi
import com.example.growwassignment.data.repository.MfRepositoryImpl
import com.example.growwassignment.data.repository.WatchlistRepositoryImpl
import com.example.growwassignment.domain.repository.MfRepository
import com.example.growwassignment.domain.repository.WatchlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMfRepository(api: MfApi): MfRepository {
        return MfRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideWatchlistRepository(dao: WatchlistDao): WatchlistRepository {
        return WatchlistRepositoryImpl(dao)
    }
}