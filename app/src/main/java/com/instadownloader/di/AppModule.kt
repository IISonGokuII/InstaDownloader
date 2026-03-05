package com.instadownloader.di

import android.content.Context
import androidx.room.Room
import com.instadownloader.data.local.AppDatabase
import com.instadownloader.data.local.SearchHistoryDao
import com.instadownloader.service.InstagramService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "instadownloader.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideInstagramService(): InstagramService {
        return InstagramService()
    }
}