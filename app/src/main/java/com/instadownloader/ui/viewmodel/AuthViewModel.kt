package com.instadownloader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instadownloader.data.preferences.UserPreferences
import com.instadownloader.data.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: InstagramRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    fun loginAnonymous() {
        viewModelScope.launch {
            prefs.setAnonymous(true)
        }
    }
}