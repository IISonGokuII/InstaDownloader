package com.instadownloader.data.repository

import com.instadownloader.data.model.InstagramMedia
import com.instadownloader.data.model.InstagramUser
import com.instadownloader.service.AuthResult
import com.instadownloader.service.InstagramService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstagramRepository @Inject constructor(
    private val api: InstagramService
) {
    suspend fun login(user: String, pass: String): AuthResult = api.login(user, pass)
    suspend fun submitTwoFactor(identifier: String, code: String): AuthResult = api.submitTwoFactor(identifier, code)
    suspend fun getUser(username: String): InstagramUser? = api.getUserProfile(username)
    suspend fun getPosts(userId: String): List<InstagramMedia> = api.getUserPosts(userId)
    suspend fun getMediaByUrl(url: String): InstagramMedia? = api.getMediaByUrl(url)
}