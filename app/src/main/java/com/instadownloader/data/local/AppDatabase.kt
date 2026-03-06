package com.instadownloader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters

@Database(entities = [SearchHistoryEntity::class, DownloadTaskEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun downloadTaskDao(): DownloadTaskDao
}