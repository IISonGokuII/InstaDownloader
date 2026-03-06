package com.instadownloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.instadownloader.ui.theme.Obsidian
import com.instadownloader.ui.theme.instagramGradient

import androidx.compose.ui.tooling.preview.Preview
import com.instadownloader.ui.theme.InstaDownloaderTheme

@Composable
fun StoryRingAvatar(imageUrl: String, hasStory: Boolean, size: Dp = 80.dp) {
    Box(contentAlignment = Alignment.Center) {
        if (hasStory) {
            Box(modifier = Modifier.size(size + 6.dp).clip(CircleShape).background(instagramGradient))
            Box(modifier = Modifier.size(size + 2.dp).clip(CircleShape).background(Obsidian))
        }
        AsyncImage(
            model = imageUrl, contentDescription = null,
            modifier = Modifier.size(size).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
fun StoryRingAvatarPreview() {
    InstaDownloaderTheme {
        StoryRingAvatar(imageUrl = "", hasStory = true)
    }
}