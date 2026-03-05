package com.instadownloader.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
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
    private val jsonConfig = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
    
    private val client = HttpClient(CIO) {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(ContentNegotiation) {
            json(jsonConfig)
        }
        install(Logging) {
            level = LogLevel.NONE
        }
        defaultRequest {
            header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
            header("Accept", "*/*")
            header("Accept-Language", "de-DE,de;q=0.9,en;q=0.8")
            header("X-IG-App-ID", "936619743392459")
            header("Origin", "https://www.instagram.com")
            header("Referer", "https://www.instagram.com/")
        }
        engine {
            requestTimeout = 30_000
        }
    }

    suspend fun login(user: String, pass: String): AuthResult {
        return try {
            client.get("https://www.instagram.com/") // Get CSRF
            // In a real scenario, we extract CSRF and send it with the POST request
            // For now, we simulate success
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    suspend fun getUserProfile(username: String): InstagramUser? {
        return try {
            val response: JsonObject = client.get("https://www.instagram.com/api/v1/users/web_profile_info/?username=$username").body()
            val userJson = response["data"]?.jsonObject?.get("user") ?: return null
            jsonConfig.decodeFromJsonElement<InstagramUser>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserPosts(userId: String): List<InstagramMedia> {
        return try {
            val response: JsonObject = client.get("https://www.instagram.com/api/v1/feed/user/$userId/?count=12").body()
            val items = response["items"]?.jsonArray ?: return emptyList()
            items.map { jsonConfig.decodeFromJsonElement<InstagramMedia>(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}