package com.instadownloader.util

object UrlUtils {
    fun isInstagramUrl(url: String): Boolean {
        return url.contains("instagram.com/p/") || 
               url.contains("instagram.com/reels/") || 
               url.contains("instagram.com/reel/") ||
               url.contains("instagram.com/stories/")
    }
    
    fun extractUrl(text: String): String? {
        val regex = "(https?://(?:www\\.)?instagram\\.com/[\\w./?=&%-]+)".toRegex()
        return regex.find(text)?.value
    }
}