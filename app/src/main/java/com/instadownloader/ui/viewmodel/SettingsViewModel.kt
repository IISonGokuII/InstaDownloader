package com.instadownloader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instadownloader.data.local.SearchHistoryDao
import com.instadownloader.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val historyDao: SearchHistoryDao
) : ViewModel() {

    val isHdQuality = prefs.isHdQuality
    val isClipboardMonitor = prefs.isClipboardMonitor

    fun setHdQuality(isHd: Boolean) {
        viewModelScope.launch {
            prefs.setHdQuality(isHd)
        }
    }

    fun setClipboardMonitor(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setClipboardMonitor(enabled)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }
}