package com.instadownloader.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.instadownloader.data.preferences.UserPreferences
import com.instadownloader.ui.viewmodel.SettingsViewModel

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    initialUrl: String? = null,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // Jump to search if we have a shared URL
    LaunchedEffect(initialUrl) {
        if (initialUrl != null) {
            selectedTabIndex = 0
        }
    }
    val isAnonymous by settingsViewModel.prefs.isAnonymous.collectAsState(initial = false)

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
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isAnonymous) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp)
                ) {
                    Text(
                        "Anonymer Modus – Nur öffentliche Profile durchsuchbar",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTabIndex) {
                    0 -> SearchScreen(initialUrl = initialUrl)
                    1 -> DownloadsScreen()
                    2 -> GalleryScreen()
                    3 -> SettingsScreen(onLogout = onLogout)
                }
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