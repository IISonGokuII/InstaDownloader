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

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val downloadManager: DownloadManager
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

        return try {
            downloadManager.downloadFile(url, filename, category) { progress ->
                setProgressAsync(workDataOf(KEY_PROGRESS to progress))
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
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