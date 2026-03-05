package com.instadownloader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("downloads", "Downloads", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun downloadFile(url: String, filename: String, category: String, onProgress: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val appDir = File(dcimDir, "InstaDownloader")
            if (!appDir.exists()) appDir.mkdirs()

            val catDir = File(appDir, category)
            if (!catDir.exists()) catDir.mkdirs()
            
            val nomedia = File(catDir, ".nomedia")
            if (!nomedia.exists()) nomedia.createNewFile()

            val file = File(catDir, filename)
            val connection = URL(url).openConnection()
            connection.connect()

            val fileLength = connection.contentLength
            val input = connection.getInputStream()
            val output = FileOutputStream(file)

            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int
            var lastProgress = 0

            while (input.read(data).also { count = it } != -1) {
                total += count
                output.write(data, 0, count)
                val progress = (total * 100 / fileLength).toInt()
                if (progress > lastProgress) {
                    onProgress(progress)
                    lastProgress = progress
                }
            }
            output.flush()
            output.close()
            input.close()
            
            // Send success notification
            val notification = NotificationCompat.Builder(context, "downloads")
                .setContentTitle("Download success")
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .build()
            notificationManager.notify(filename.hashCode(), notification)
        }
    }
}