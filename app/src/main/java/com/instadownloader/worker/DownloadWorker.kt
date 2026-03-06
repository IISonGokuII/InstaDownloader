package com.instadownloader.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.instadownloader.service.DownloadManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

import com.instadownloader.data.local.DownloadStatus
import com.instadownloader.data.local.DownloadTaskDao

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val downloadManager: DownloadManager,
    private val downloadTaskDao: DownloadTaskDao
) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, "downloads")
            .setContentTitle("Downloading")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())
        val url      = inputData.getString(KEY_URL)      ?: return Result.failure()
        val filename = inputData.getString(KEY_FILENAME) ?: return Result.failure()
        val category = inputData.getString(KEY_CATEGORY) ?: return Result.failure()

        downloadTaskDao.updateStatus(id.toString(), DownloadStatus.DOWNLOADING, null)

        return try {
            downloadManager.downloadFile(url, filename, category) { progress ->
                setProgressAsync(workDataOf(KEY_PROGRESS to progress))
                // Also update Room for the History Tab
                CoroutineScope(Dispatchers.IO).launch {
                    downloadTaskDao.updateProgress(id.toString(), DownloadStatus.DOWNLOADING, progress)
                }
            }
            downloadTaskDao.updateStatus(id.toString(), DownloadStatus.COMPLETED, null)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                downloadTaskDao.updateStatus(id.toString(), DownloadStatus.FAILED, e.message)
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_URL      = "url"
        const val KEY_FILENAME = "filename"
        const val KEY_CATEGORY = "category"
        const val KEY_PROGRESS = "progress"
        const val NOTIFICATION_ID = 1001
    }
}