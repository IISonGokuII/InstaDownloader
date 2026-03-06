package com.instadownloader.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.instadownloader.ui.components.VideoPlayer
import com.instadownloader.ui.viewmodel.GalleryFile
import com.instadownloader.ui.viewmodel.GalleryFolder
import com.instadownloader.ui.viewmodel.GalleryViewModel
import java.io.File
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val folders by viewModel.folders.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val currentFiles by viewModel.currentFolderFiles.collectAsState()
    val selectedFiles by viewModel.selectedFiles.collectAsState()
    val isSelectionMode = selectedFiles.isNotEmpty()
    val context = LocalContext.current
    
    var fullscreenMediaIndex by remember { mutableStateOf<Int?>(null) }

    BackHandler(selectedFolder != null || isSelectionMode) {
        if (isSelectionMode) {
            viewModel.clearSelection()
        } else if (selectedFolder != null) {
            viewModel.selectFolder(null)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    if (isSelectionMode) "${selectedFiles.size} ausgewählt"
                    else selectedFolder ?: "Galerie"
                )
            },
            navigationIcon = {
                if (selectedFolder != null && !isSelectionMode) {
                    IconButton(onClick = { viewModel.selectFolder(null) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                }
            },
            actions = {
                if (isSelectionMode) {
                    IconButton(onClick = { viewModel.saveToPublicGallery(context) }) {
                        Icon(Icons.Default.Save, contentDescription = "In Galerie speichern")
                    }
                    IconButton(onClick = { viewModel.deleteSelected() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Löschen")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (selectedFolder == null) {
            FolderGrid(folders) { viewModel.selectFolder(it.name) }
        } else {
            var selectedFilter by remember { mutableStateOf("Alle") }
            val filters = listOf("Alle", "Stories", "Reels/Posts", "Highlights")
            
            Column {
                ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(selectedFilter),
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filters.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedFilter == title,
                            onClick = { selectedFilter = title },
                            text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val filteredFiles = currentFiles.filter { file ->
                    when (selectedFilter) {
                        "Stories" -> file.file.name.startsWith("story_")
                        "Reels/Posts" -> file.file.name.startsWith("vid_") || file.file.name.startsWith("img_") || file.file.name.startsWith("car_")
                        "Highlights" -> file.file.name.startsWith("highlight_")
                        else -> true
                    }
                }

                MediaGrid(
                    files = filteredFiles,
                    selectedFiles = selectedFiles,
                    onToggleSelection = { viewModel.toggleSelection(it) },
                    onClick = { index -> 
                        val clickedFile = filteredFiles[index]
                        if (isSelectionMode) viewModel.toggleSelection(clickedFile.file)
                        else fullscreenMediaIndex = currentFiles.indexOf(clickedFile)
                    }
                )
            }
        }
    }

    if (fullscreenMediaIndex != null) {
        FullscreenMediaViewer(
            files = currentFiles,
            initialIndex = fullscreenMediaIndex!!,
            onDismiss = { fullscreenMediaIndex = null }
        )
    }
}

@Composable
fun FolderGrid(folders: List<GalleryFolder>, onFolderClick: (GalleryFolder) -> Unit) {
    if (folders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Ordner gefunden", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(folders) { folder ->
                FolderItem(folder) { onFolderClick(folder) }
            }
        }
    }
}

@Composable
fun FolderItem(folder: GalleryFolder, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        AsyncImage(
            model = folder.thumbnailFile,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(folder.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        Text("${folder.fileCount} Medien", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGrid(
    files: List<GalleryFile>,
    selectedFiles: Set<File>,
    onToggleSelection: (File) -> Unit,
    onClick: (Int) -> Unit
) {
    if (files.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Dieser Ordner ist leer", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(files.size) { index ->
                val item = files[index]
                val isSelected = selectedFiles.contains(item.file)
                GalleryItem(
                    item = item,
                    isSelected = isSelected,
                    onLongClick = { onToggleSelection(item.file) },
                    onClick = { onClick(index) }
                )
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
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .padding(4.dp),
                tint = Color.White
            )
        }
        
        // Add badge for category based on filename
        val badgeText = when {
            item.file.name.startsWith("story_") -> "Story"
            item.file.name.startsWith("vid_") || item.file.name.startsWith("car_") -> "Reel/Post"
            item.file.name.startsWith("highlight_") -> "Highlight"
            else -> "Bild"
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(badgeText, style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullscreenMediaViewer(
    files: List<GalleryFile>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { files.size })
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                TopAppBar(
                    title = { Text(files[pagerState.currentPage].file.name, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Schließen", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(padding)
            ) { page ->
                val file = files[page]
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (file.isVideo) {
                        VideoPlayer(file.file)
                    } else {
                        var scale by remember { mutableStateOf(1f) }
                        var offset by remember { mutableStateOf(Offset.Zero) }
                        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                offset += offsetChange
                            } else {
                                offset = Offset.Zero
                            }
                        }

                        AsyncImage(
                            model = file.file,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                .transformable(state = state),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}