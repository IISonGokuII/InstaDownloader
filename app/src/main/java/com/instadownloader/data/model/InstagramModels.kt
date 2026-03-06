package com.instadownloader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InstagramUser(
    val pk: String = "",
    val username: String = "",
    val full_name: String = "",
    val profile_pic_url: String = "",
    val profile_pic_url_hd: String = "",
    val is_private: Boolean = false,
    val follower_count: Int = 0,
    val following_count: Int = 0,
    val media_count: Int = 0
)

@Serializable
data class InstagramMedia(
    val id: String = "",
    val media_type: Int = 1, // 1=Image, 2=Video, 8=Carousel
    val image_versions2: ImageVersions? = null,
    val video_versions: List<VideoVersion>? = null,
    val carousel_media: List<CarouselMedia>? = null,
    val user: InstagramUser? = null
)

@Serializable
data class ImageVersions(
    val candidates: List<ImageCandidate> = emptyList()
)

@Serializable
data class ImageCandidate(
    val width: Int,
    val height: Int,
    val url: String
)

@Serializable
data class VideoVersion(
    val width: Int,
    val height: Int,
    val url: String,
    val id: String = ""
)

@Serializable
data class CarouselMedia(
    val id: String,
    val media_type: Int,
    val image_versions2: ImageVersions? = null,
    val video_versions: List<VideoVersion>? = null
)

@Serializable
data class Highlight(
    val id: String,
    val title: String,
    val cover_media: CoverMedia? = null
)

@Serializable
data class CoverMedia(
    val cropped_image_version: ImageCandidate? = null
)
