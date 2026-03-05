package com.instadownloader.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.instadownloader.ui.viewmodel.GalleryFile
import com.instadownloader.ui.viewmodel.GalleryViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val files by viewModel.files.collectAsState()
    val selectedFiles by viewModel.selectedFiles.collectAsState()
    val isSelectionMode = selectedFiles.isNotEmpty()

    BackHandler(isSelectionMode) {
        viewModel.clearSelection()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isSelectionMode) {
            TopAppBar(
                title = { Text("${selectedFiles.size} ausgewählt") },
                actions = {
                    IconButton(onClick = { viewModel.deleteSelected() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Löschen")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Keine Downloads gefunden", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(files) { item ->
                    val isSelected = selectedFiles.contains(item.file)
                    GalleryItem(
                        item = item,
                        isSelected = isSelected,
                        onLongClick = { viewModel.toggleSelection(item.file) },
                        onClick = {
                            if (isSelectionMode) {
                                viewModel.toggleSelection(item.file)
                            } else {
                                // TODO: Open full screen viewer or video player
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryItem(
    item: GalleryFile,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        AsyncImage(
            model = item.file,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (item.isVideo) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                tint = Color.White
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            )
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}