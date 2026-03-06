package com.instadownloader.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.instadownloader.ui.theme.Surface3
import com.instadownloader.ui.theme.instagramGradient

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import com.instadownloader.ui.theme.InstaDownloaderTheme

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) instagramGradient else Brush.linearGradient(listOf(Surface3, Surface3)))
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = isLoading, label = "button_loading") { loading ->
            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            else Text(text, color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview
@Composable
fun GradientButtonPreview() {
    InstaDownloaderTheme {
        GradientButton(
            text = "Anmelden",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}