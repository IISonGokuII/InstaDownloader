package com.instadownloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.instadownloader.ui.theme.glassBorderGradient
import com.instadownloader.ui.theme.glassGradient

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import com.instadownloader.ui.theme.InstaDownloaderTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(glassGradient)
            .border(width = 0.5.dp, brush = glassBorderGradient, shape = shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) { Column(content = content) }
}

@Preview
@Composable
fun GlassCardPreview() {
    InstaDownloaderTheme {
        GlassCard(modifier = Modifier.padding(16.dp)) {
            Text("Test Content in Glass Card", color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(16.dp))
        }
    }
}