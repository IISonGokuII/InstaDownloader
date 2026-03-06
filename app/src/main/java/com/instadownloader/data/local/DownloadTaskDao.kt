package com.instadownloader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadTaskDao {
    @Query("SELECT * FROM download_tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<DownloadTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DownloadTaskEntity)

    @Query("UPDATE download_tasks SET status = :status, progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: String, status: DownloadStatus, progress: Int)

    @Query("UPDATE download_tasks SET status = :status, errorMessage = :error WHERE id = :id")
    suspend fun updateStatus(id: String, status: DownloadStatus, error: String?)

    @Query("DELETE FROM download_tasks WHERE status = 'COMPLETED' OR status = 'FAILED'")
    suspend fun clearHistory()

    @Query("DELETE FROM download_tasks")
    suspend fun deleteAll()
}