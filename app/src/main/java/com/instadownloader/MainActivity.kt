package com.instadownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import com.instadownloader.ui.theme.InstaDownloaderTheme
import com.instadownloader.ui.navigation.NavGraph

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import com.instadownloader.service.ClipboardMonitorService
import com.instadownloader.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Monitor clipboard preference and start/stop service
        lifecycleScope.launch {
            prefs.isClipboardMonitor.collect { enabled ->
                val intent = Intent(this@MainActivity, ClipboardMonitorService::class.java)
                if (enabled) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                } else {
                    stopService(intent)
                }
            }
        }

        setContent {
            InstaDownloaderTheme {
                NavGraph()
            }
        }
    }
}