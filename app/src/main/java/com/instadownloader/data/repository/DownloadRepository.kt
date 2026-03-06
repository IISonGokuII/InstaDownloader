package com.instadownloader.data.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.instadownloader.data.local.DownloadStatus
import com.instadownloader.data.local.DownloadTaskDao
import com.instadownloader.data.local.DownloadTaskEntity
import com.instadownloader.worker.DownloadWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class DownloadRepository @Inject constructor(
    private val downloadTaskDao: DownloadTaskDao,
    @ApplicationContext private val context: Context
) {
    val allTasks: Flow<List<DownloadTaskEntity>> = downloadTaskDao.getAllTasks()

    suspend fun enqueueDownload(url: String, filename: String, category: String) {
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(
                DownloadWorker.KEY_URL to url,
                DownloadWorker.KEY_FILENAME to filename,
                DownloadWorker.KEY_CATEGORY to category
            ))
            .build()
        
        val task = DownloadTaskEntity(
            id = workRequest.id.toString(),
            url = url,
            filename = filename,
            category = category,
            status = DownloadStatus.QUEUED
        )
        
        downloadTaskDao.insertTask(task)
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    suspend fun clearHistory() {
        downloadTaskDao.clearHistory()
    }
}