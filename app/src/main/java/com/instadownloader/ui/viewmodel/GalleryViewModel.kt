package com.instadownloader.ui.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class GalleryFile(
    val file: File,
    val isVideo: Boolean
)

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _files = MutableStateFlow<List<GalleryFile>>(emptyList())
    val files: StateFlow<List<GalleryFile>> = _files.asStateFlow()

    private val _selectedFiles = MutableStateFlow<Set<File>>(emptySet())
    val selectedFiles: StateFlow<Set<File>> = _selectedFiles.asStateFlow()

    init {
        loadFiles()
    }

    fun loadFiles() {
        viewModelScope.launch {
            val downloadedFiles = withContext(Dispatchers.IO) {
                val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val appDir = File(dcimDir, "InstaDownloader")
                
                val allFiles = mutableListOf<GalleryFile>()
                if (appDir.exists()) {
                    appDir.listFiles()?.forEach { categoryDir ->
                        if (categoryDir.isDirectory) {
                            categoryDir.listFiles()?.forEach { file ->
                                if (file.isFile && !file.name.startsWith(".")) {
                                    val isVideo = file.extension.lowercase() in listOf("mp4", "mkv", "mov")
                                    allFiles.add(GalleryFile(file, isVideo))
                                }
                            }
                        }
                    }
                }
                allFiles.sortedByDescending { it.file.lastModified() }
            }
            _files.value = downloadedFiles
        }
    }

    fun toggleSelection(file: File) {
        val current = _selectedFiles.value.toMutableSet()
        if (current.contains(file)) {
            current.remove(file)
        } else {
            current.add(file)
        }
        _selectedFiles.value = current
    }

    fun clearSelection() {
        _selectedFiles.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _selectedFiles.value.forEach { file ->
                    if (file.exists()) file.delete()
                }
            }
            clearSelection()
            loadFiles()
        }
    }
}