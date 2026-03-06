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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.instadownloader.R
import com.instadownloader.ui.theme.InstaDownloaderTheme
import com.instadownloader.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isHdQuality by viewModel.prefs.isHdQuality.collectAsState(initial = true)
    val isClipboardMonitor by viewModel.prefs.isClipboardMonitor.collectAsState(initial = false)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        SettingsSection(stringResource(R.string.settings_general)) {
            SettingsToggleItem(
                title = stringResource(R.string.settings_hd_quality),
                subtitle = stringResource(R.string.settings_hd_desc),
                icon = Icons.Default.HighQuality,
                checked = isHdQuality,
                onCheckedChange = { viewModel.setHdQuality(it) }
            )
            SettingsToggleItem(
                title = stringResource(R.string.settings_clipboard),
                subtitle = stringResource(R.string.settings_clipboard_desc),
                icon = Icons.Default.MonitorHeart,
                checked = isClipboardMonitor,
                onCheckedChange = { viewModel.setClipboardMonitor(it) }
            )
        }

        SettingsSection(stringResource(R.string.settings_data)) {
            SettingsClickItem(
                title = stringResource(R.string.settings_clear_history),
                subtitle = stringResource(R.string.settings_clear_history_desc),
                icon = Icons.Default.DeleteSweep,
                onClick = { viewModel.clearHistory() }
            )
        }

        SettingsSection(stringResource(R.string.settings_about)) {
            SettingsClickItem(
                title = "Version",
                subtitle = "1.0.0 (Build 2026)",
                icon = Icons.Default.Info,
                onClick = {}
            )
            SettingsClickItem(
                title = stringResource(R.string.settings_logout),
                subtitle = stringResource(R.string.settings_logout_desc),
                icon = Icons.Default.DeleteSweep,
                onClick = onLogout
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
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
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

@Preview
@Composable
fun SettingsScreenPreview() {
    InstaDownloaderTheme {
        SettingsScreen(onLogout = {})
    }
}