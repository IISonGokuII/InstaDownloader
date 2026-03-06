package com.instadownloader.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String {
        return status.name
    }

    @TypeConverter
    fun toDownloadStatus(status: String): DownloadStatus {
        return DownloadStatus.valueOf(status)
    }
}