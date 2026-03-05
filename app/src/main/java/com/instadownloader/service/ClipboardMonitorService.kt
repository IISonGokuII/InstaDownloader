package com.instadownloader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.instadownloader.MainActivity
import com.instadownloader.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClipboardMonitorService : Service() {

    private lateinit var clipboardManager: ClipboardManager
    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        val clipData = clipboardManager.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text?.toString() ?: ""
            if (isInstagramUrl(text)) {
                showDownloadNotification(text)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener(clipListener)
        startForegroundService()
    }

    private fun startForegroundService() {
        val channelId = "clipboard_monitor"
        val channelName = "Clipboard Monitor"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("InstaDownloader")
            .setContentText("Clipboard Monitor aktiv")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .build()

        startForeground(1002, notification)
    }

    private fun showDownloadNotification(url: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("url", url)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "clipboard_monitor")
            .setContentTitle("Instagram Link erkannt")
            .setContentText("Klicken zum Herunterladen")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(url.hashCode(), notification)
    }

    private fun isInstagramUrl(url: String): Boolean {
        return url.contains("instagram.com/p/") || 
               url.contains("instagram.com/reels/") || 
               url.contains("instagram.com/reel/") ||
               url.contains("instagram.com/stories/")
    }

    override fun onDestroy() {
        clipboardManager.removePrimaryClipChangedListener(clipListener)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}