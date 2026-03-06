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

import com.instadownloader.util.UrlUtils
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: UserPreferences
    
    private var sharedUrl by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)

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
                NavGraph(prefs = prefs, initialUrl = sharedUrl)
                // Clear shared URL after consumption
                LaunchedEffect(sharedUrl) {
                    if (sharedUrl != null) sharedUrl = null
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                sharedUrl = UrlUtils.extractUrl(text)
            }
        } else if (Intent.ACTION_VIEW == action) {
            intent.dataString?.let { data ->
                if (UrlUtils.isInstagramUrl(data)) {
                    sharedUrl = data
                }
            }
        } else if (intent.hasExtra("url")) {
            // From notification
            sharedUrl = intent.getStringExtra("url")
        }
    }
}