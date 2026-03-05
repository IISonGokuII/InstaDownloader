package com.instadownloader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf(
        TabItem("Suchen", Icons.Default.Search),
        TabItem("Downloads", Icons.Default.Download),
        TabItem("Galerie", Icons.Default.PhotoLibrary),
        TabItem("Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> SearchScreen()
                1 -> DownloadsScreen()
                2 -> GalleryScreen()
                3 -> SettingsScreen()
            }
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun DownloadsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Download History wird im nächsten Update implementiert")
    }
}