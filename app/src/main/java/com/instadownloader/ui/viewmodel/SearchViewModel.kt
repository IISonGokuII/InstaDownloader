package com.instadownloader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instadownloader.data.local.SearchHistoryDao
import com.instadownloader.data.local.SearchHistoryEntity
import com.instadownloader.data.model.InstagramMedia
import com.instadownloader.data.model.InstagramUser
import com.instadownloader.data.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val user: InstagramUser, val posts: List<InstagramMedia>) : SearchUiState()
    data class SuccessMedia(val media: InstagramMedia) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: InstagramRepository,
    private val historyDao: SearchHistoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val searchHistory = historyDao.getAllHistory()

    fun searchUser(username: String) {
        if (username.contains("instagram.com")) {
            searchByUrl(username)
            return
        }
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val user = repository.getUser(username)
                if (user != null) {
                    val posts = repository.getPosts(user.pk)
                    _uiState.value = SearchUiState.Success(user, posts)
                    
                    // Add to history
                    historyDao.insertSearch(
                        SearchHistoryEntity(
                            username = user.username,
                            profilePicUrl = user.profile_pic_url,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } else {
                    _uiState.value = SearchUiState.Error("Benutzer nicht gefunden. Profil evtl. privat oder blockiert.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SearchUiState.Error("Netzwerkfehler: ${e.message}")
            }
        }
    }

    fun searchByUrl(url: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val media = repository.getMediaByUrl(url)
                if (media != null) {
                    _uiState.value = SearchUiState.SuccessMedia(media)
                } else {
                    _uiState.value = SearchUiState.Error("Inhalt konnte nicht geladen werden. Link evtl. ungültig oder privat.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SearchUiState.Error("Netzwerkfehler beim Laden des Links")
            }
        }
    }

    fun deleteHistory(username: String) {
        viewModelScope.launch {
            historyDao.deleteSearch(username)
        }
    }

    fun toggleFavorite(username: String, isFavorite: Boolean) {
        viewModelScope.launch {
            historyDao.updateFavorite(username, isFavorite)
        }
    }
}