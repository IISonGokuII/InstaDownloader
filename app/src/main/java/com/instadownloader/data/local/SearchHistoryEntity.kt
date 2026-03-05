package com.instadownloader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val username: String,
    val profilePicUrl: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)