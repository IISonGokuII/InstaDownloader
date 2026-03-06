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

data class GalleryFolder(
    val name: String,
    val thumbnailFile: File?,
    val fileCount: Int
)

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _folders = MutableStateFlow<List<GalleryFolder>>(emptyList())
    val folders: StateFlow<List<GalleryFolder>> = _folders.asStateFlow()

    private val _currentFolderFiles = MutableStateFlow<List<GalleryFile>>(emptyList())
    val currentFolderFiles: StateFlow<List<GalleryFile>> = _currentFolderFiles.asStateFlow()

    private val _selectedFolder = MutableStateFlow<String?>(null)
    val selectedFolder: StateFlow<String?> = _selectedFolder.asStateFlow()

    private val _selectedFiles = MutableStateFlow<Set<File>>(emptySet())
    val selectedFiles: StateFlow<Set<File>> = _selectedFiles.asStateFlow()

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            val foldersList = withContext(Dispatchers.IO) {
                val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val appDir = File(dcimDir, "InstaDownloader")
                
                val folders = mutableListOf<GalleryFolder>()
                if (appDir.exists()) {
                    appDir.listFiles()?.filter { it.isDirectory }?.forEach { dir ->
                        val files = dir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") } ?: emptyList<File>()
                        if (files.isNotEmpty()) {
                            folders.add(GalleryFolder(
                                name = dir.name,
                                thumbnailFile = files.maxByOrNull { it.lastModified() },
                                fileCount = files.size
                            ))
                        }
                    }
                }
                folders.sortedByDescending { it.thumbnailFile?.lastModified() ?: 0L }
            }
            _folders.value = foldersList
        }
    }

    fun selectFolder(folderName: String?) {
        _selectedFolder.value = folderName
        if (folderName != null) {
            loadFilesFromFolder(folderName)
        } else {
            _currentFolderFiles.value = emptyList()
            loadFolders()
        }
    }

    private fun loadFilesFromFolder(folderName: String) {
        viewModelScope.launch {
            val filesList = withContext(Dispatchers.IO) {
                val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val appDir = File(dcimDir, "InstaDownloader")
                val targetDir = File(appDir, folderName)
                
                val allFiles = mutableListOf<GalleryFile>()
                if (targetDir.exists()) {
                    targetDir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") }?.forEach { file ->
                        val isVideo = file.extension.lowercase() in listOf("mp4", "mkv", "mov")
                        allFiles.add(GalleryFile(file, isVideo))
                    }
                }
                allFiles.sortedByDescending { it.file.lastModified() }
            }
            _currentFolderFiles.value = filesList
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

    fun saveToPublicGallery(context: android.content.Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _selectedFiles.value.forEach { file ->
                    val dcimPublic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    val targetFile = File(dcimPublic, file.name)
                    file.copyTo(targetFile, overwrite = true)
                    
                    // Trigger Media Scanner
                    android.media.MediaScannerConnection.scanFile(
                        context,
                        arrayOf(targetFile.absolutePath),
                        null,
                        null
                    )
                }
            }
            clearSelection()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _selectedFiles.value.forEach { file ->
                    if (file.exists()) file.delete()
                }
            }
            clearSelection()
            val folder = _selectedFolder.value
            if (folder != null) {
                loadFilesFromFolder(folder)
            } else {
                loadFolders()
            }
        }
    }
}