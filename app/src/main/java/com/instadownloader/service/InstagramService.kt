package com.instadownloader.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import com.instadownloader.data.model.InstagramUser
import com.instadownloader.data.model.InstagramMedia

sealed class AuthResult {
    object Success : AuthResult()
    enum class TwoFactorMethod { AUTHENTICATOR_APP, SMS }
    data class TwoFactorRequired(val identifier: String, val method: TwoFactorMethod) : AuthResult()
    data class CheckpointRequired(val checkpointUrl: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class InstagramService {
    private val client = HttpClient(CIO) {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true })
        }
        install(Logging) {
            level = LogLevel.NONE
        }
        defaultRequest {
            header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36")
            header("Accept", "*/*")
            header("Accept-Language", "de-DE,de;q=0.9")
            header("X-IG-App-ID", "936619743392459")
            header("Origin", "https://www.instagram.com")
            header("Referer", "https://www.instagram.com/")
        }
        engine {
            requestTimeout = 30_000
        }
    }

    suspend fun login(user: String, pass: String): AuthResult = AuthResult.Success
    suspend fun getUserProfile(username: String): InstagramUser? = null
    suspend fun getUserPosts(userId: String): List<InstagramMedia> = emptyList()
}