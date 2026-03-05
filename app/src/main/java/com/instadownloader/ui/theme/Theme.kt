package com.instadownloader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun InstaDownloaderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary          = GradMid,
            onPrimary        = Color.White,
            primaryContainer = GradStart.copy(alpha = 0.2f),
            background       = Obsidian,
            surface          = Surface1,
            surfaceVariant   = Surface2,
            onBackground     = TextPrimary,
            onSurface        = TextPrimary,
            onSurfaceVariant = TextSecondary,
            error            = StatusError,
            outline          = Surface3
        ),
        typography = AppTypography,
        content    = content
    )
}