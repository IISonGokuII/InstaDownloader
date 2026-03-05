package com.instadownloader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY isFavorite DESC, timestamp DESC")
    fun getAllHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE username = :username")
    suspend fun deleteSearch(username: String)

    @Query("UPDATE search_history SET isFavorite = :isFavorite WHERE username = :username")
    suspend fun updateFavorite(username: String, isFavorite: Boolean)
}