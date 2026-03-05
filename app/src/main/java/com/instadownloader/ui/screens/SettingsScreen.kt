package com.instadownloader.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.instadownloader.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isHdQuality by viewModel.isHdQuality.collectAsState(initial = true)
    val isClipboardMonitor by viewModel.isClipboardMonitor.collectAsState(initial = false)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Einstellungen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        SettingsSection("Allgemein") {
            SettingsToggleItem(
                title = "HD Qualität",
                subtitle = "Bilder und Videos in höchster Qualität laden",
                icon = Icons.Default.HighQuality,
                checked = isHdQuality,
                onCheckedChange = { viewModel.setHdQuality(it) }
            )
            SettingsToggleItem(
                title = "Clipboard Monitor",
                subtitle = "Instagram Links automatisch erkennen",
                icon = Icons.Default.MonitorHeart,
                checked = isClipboardMonitor,
                onCheckedChange = { viewModel.setClipboardMonitor(it) }
            )
        }

        SettingsSection("Datenverwaltung") {
            SettingsClickItem(
                title = "Suchverlauf löschen",
                subtitle = "Alle Einträge aus der Suche entfernen",
                icon = Icons.Default.DeleteSweep,
                onClick = { viewModel.clearHistory() }
            )
        }

        SettingsSection("Über") {
            SettingsClickItem(
                title = "Version",
                subtitle = "1.0.0 (Build 2026)",
                icon = Icons.Default.Info,
                onClick = {}
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        content()
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}

@Composable
fun SettingsClickItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}