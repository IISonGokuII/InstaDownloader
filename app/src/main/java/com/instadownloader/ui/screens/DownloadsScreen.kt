package com.instadownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.instadownloader.data.local.DownloadStatus
import com.instadownloader.data.local.DownloadTaskEntity
import com.instadownloader.ui.viewmodel.DownloadsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Download-Verlauf") },
            actions = {
                IconButton(onClick = { viewModel.clearHistory() }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Verlauf leeren")
                }
            }
        )

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Keine Downloads in der Historie", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    DownloadTaskItem(task)
                }
            }
        }
    }
}

@Composable
fun DownloadTaskItem(task: DownloadTaskEntity) {
    val dateFormat = remember { SimpleDateFormat("dd.MM, HH:mm", Locale.getDefault()) }
    val dateString = dateFormat.format(Date(task.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (task.status) {
                        DownloadStatus.COMPLETED -> Icons.Default.CheckCircle
                        DownloadStatus.FAILED -> Icons.Default.Error
                        else -> Icons.Default.Downloading
                    },
                    contentDescription = null,
                    tint = when (task.status) {
                        DownloadStatus.COMPLETED -> Color(0xFF4CAF50)
                        DownloadStatus.FAILED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.filename,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${task.category} • $dateString",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = when (task.status) {
                        DownloadStatus.QUEUED -> "Warten..."
                        DownloadStatus.DOWNLOADING -> "${task.progress}%"
                        DownloadStatus.COMPLETED -> "Fertig"
                        DownloadStatus.FAILED -> "Fehler"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (task.status == DownloadStatus.DOWNLOADING) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = task.progress / 100f,
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }
            
            if (task.status == DownloadStatus.FAILED && task.errorMessage != null) {
                Text(
                    text = task.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}