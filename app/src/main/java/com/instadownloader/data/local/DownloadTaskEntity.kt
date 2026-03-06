package com.instadownloader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_tasks")
data class DownloadTaskEntity(
    @PrimaryKey
    val id: String, // WorkManager Request ID
    val url: String,
    val filename: String,
    val category: String,
    val status: DownloadStatus,
    val progress: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)