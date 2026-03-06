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
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.call.body
import io.ktor.http.Parameters
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.plugins.plugin
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
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
            // Hole initiale Session / CSRF Token
            client.get("https://www.instagram.com/")
            
            // Lese Token aus dem Cookie Manager
            val cookies = client.plugin(HttpCookies).get(Url("https://www.instagram.com"))
            val csrfToken = cookies.find { it.name == "csrftoken" }?.value ?: ""

            val response: HttpResponse = client.post("https://www.instagram.com/accounts/login/ajax/") {
                header("x-csrftoken", csrfToken)
                header("Content-Type", "application/x-www-form-urlencoded")
                setBody(FormDataContent(
                    Parameters.build {
                        append("username", user)
                        append("enc_password", "#PWD_INSTAGRAM_BROWSER:0:${System.currentTimeMillis()}:$pass")
                        append("queryParams", "{}")
                        append("optIntoOneTap", "false")
                    }
                ))
            }
            
            val responseBody = response.bodyAsText()
            val jsonObj = jsonConfig.decodeFromString<JsonObject>(responseBody)

            when {
                jsonObj["authenticated"]?.toString()?.toBooleanStrictOrNull() == true -> AuthResult.Success
                jsonObj["two_factor_required"]?.toString()?.toBooleanStrictOrNull() == true -> {
                    val identifier = jsonObj["two_factor_info"]?.jsonObject?.get("two_factor_identifier")?.toString()?.replace("\"", "") ?: ""
                    AuthResult.TwoFactorRequired(identifier, AuthResult.TwoFactorMethod.AUTHENTICATOR_APP)
                }
                jsonObj["message"]?.toString()?.contains("checkpoint_required") == true -> {
                    val url = jsonObj["checkpoint_url"]?.toString()?.replace("\"", "") ?: ""
                    AuthResult.CheckpointRequired(url)
                }
                else -> {
                    val errorMsg = jsonObj["message"]?.toString()?.replace("\"", "") ?: "Login fehlgeschlagen"
                    AuthResult.Error(errorMsg)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.message ?: "Login-Anfrage fehlgeschlagen")
        }
    }

    suspend fun submitTwoFactor(identifier: String, code: String): AuthResult {
        return try {
            val cookies = client.plugin(HttpCookies).get(Url("https://www.instagram.com"))
            val csrfToken = cookies.find { it.name == "csrftoken" }?.value ?: ""

            val response: HttpResponse = client.post("https://www.instagram.com/accounts/login/ajax/two_factor/") {
                header("x-csrftoken", csrfToken)
                header("Content-Type", "application/x-www-form-urlencoded")
                setBody(FormDataContent(
                    Parameters.build {
                        append("identifier", identifier)
                        append("verificationCode", code)
                    }
                ))
            }

            val responseBody = response.bodyAsText()
            val jsonObj = jsonConfig.decodeFromString<JsonObject>(responseBody)

            if (jsonObj["authenticated"]?.toString()?.toBooleanStrictOrNull() == true) {
                AuthResult.Success
            } else {
                val errorMsg = jsonObj["message"]?.toString()?.replace("\"", "") ?: "2FA fehlgeschlagen"
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.message ?: "2FA-Anfrage fehlgeschlagen")
        }
    }

    suspend fun getUserProfile(username: String): InstagramUser? {
        return try {
            val response: JsonObject = client.get("https://www.instagram.com/api/v1/users/web_profile_info/?username=$username").body()
            val userJson = response["data"]?.jsonObject?.get("user") ?: return null
            jsonConfig.decodeFromJsonElement<InstagramUser>(userJson)
        } catch (e: Exception) {
            e.printStackTrace()
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