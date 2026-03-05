package com.instadownloader.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val IS_HD_QUALITY = booleanPreferencesKey("is_hd_quality")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val IS_ANONYMOUS = booleanPreferencesKey("is_anonymous")

    val isHdQuality: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_HD_QUALITY] ?: true
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val isAnonymous: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_ANONYMOUS] ?: false
    }

    suspend fun setHdQuality(isHd: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_HD_QUALITY] = isHd
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun setAnonymous(anonymous: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ANONYMOUS] = anonymous
        }
    }
}