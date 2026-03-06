package com.instadownloader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instadownloader.data.preferences.UserPreferences
import com.instadownloader.data.repository.InstagramRepository
import com.instadownloader.service.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: InstagramRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    fun login(username: String, pass: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(username, pass)
            if (result is AuthResult.Success) {
                prefs.setLoggedIn(true)
                prefs.setAnonymous(false)
            }
            onResult(result)
        }
    }

    fun submitTwoFactor(identifier: String, code: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            val result = repository.submitTwoFactor(identifier, code)
            if (result is AuthResult.Success) {
                prefs.setLoggedIn(true)
                prefs.setAnonymous(false)
            }
            onResult(result)
        }
    }

    fun loginAnonymous() {
        viewModelScope.launch {
            prefs.setAnonymous(true)
            prefs.setLoggedIn(false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.setAnonymous(false)
            prefs.setLoggedIn(false)
            // Ideally clear cookies from Ktor here
        }
    }
}