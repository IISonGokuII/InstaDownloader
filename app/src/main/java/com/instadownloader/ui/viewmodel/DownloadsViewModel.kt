package com.instadownloader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instadownloader.data.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: DownloadRepository
) : ViewModel() {

    val tasks = repository.allTasks

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}