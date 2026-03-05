package com.instadownloader.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Obsidian      = Color(0xFF080808)
val Surface1      = Color(0xFF111111)
val Surface2      = Color(0xFF1A1A1A)
val Surface3      = Color(0xFF222222)
val GlassSurface  = Color(0x1AFFFFFF)

val GradStart     = Color(0xFF833AB4)
val GradMid       = Color(0xFFE1306C)
val GradEnd       = Color(0xFFFCAF45)

val TextPrimary   = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFF9E9E9E)
val TextTertiary  = Color(0xFF616161)

val StatusSuccess = Color(0xFF4CAF50)
val StatusError   = Color(0xFFCF6679)
val StatusWarning = Color(0xFFFFB74D)

val instagramGradient = Brush.linearGradient(
    colors = listOf(GradStart, GradMid, GradEnd)
)
val subtleGradient = Brush.linearGradient(
    colors = listOf(GradStart.copy(alpha = 0.3f), GradEnd.copy(alpha = 0.1f))
)
val glassGradient = Brush.linearGradient(
    colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.03f))
)
val glassBorderGradient = Brush.linearGradient(
    colors = listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.04f))
)