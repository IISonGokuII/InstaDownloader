package com.instadownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.instadownloader.data.local.SearchHistoryEntity
import com.instadownloader.data.model.InstagramMedia
import com.instadownloader.data.model.InstagramUser
import com.instadownloader.ui.components.GlassCard
import com.instadownloader.ui.components.StoryRingAvatar
import com.instadownloader.ui.viewmodel.SearchUiState
import com.instadownloader.ui.viewmodel.SearchViewModel
import com.instadownloader.worker.DownloadWorker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.searchHistory.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Benutzername suchen...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (searchQuery.isNotEmpty()) viewModel.searchUser(searchQuery)
            }),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        when (val state = uiState) {
            is SearchUiState.Idle -> {
                SearchHistoryList(
                    history = history,
                    onItemClick = { 
                        searchQuery = it
                        viewModel.searchUser(it)
                    },
                    onDelete = { viewModel.deleteHistory(it) },
                    onFavorite = { username, fav -> viewModel.toggleFavorite(username, fav) }
                )
            }
            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchUiState.Success -> {
                UserDetailContent(state.user, state.posts)
            }
            is SearchUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun SearchHistoryList(
    history: List<SearchHistoryEntity>,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onFavorite: (String, Boolean) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                "Verlauf",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(history) { item ->
            ListItem(
                headlineContent = { Text(item.username) },
                leadingContent = {
                    AsyncImage(
                        model = item.profilePicUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                },
                trailingContent = {
                    Row {
                        IconButton(onClick = { onFavorite(item.username, !item.isFavorite) }) {
                            Icon(
                                if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (item.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDelete(item.username) }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.clickable { onItemClick(item.username) }
            )
        }
    }
}

@Composable
fun UserDetailContent(user: InstagramUser, posts: List<InstagramMedia>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // User Header
        GlassCard(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                StoryRingAvatar(imageUrl = user.profile_pic_url, hasStory = false, size = 64.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user.username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(user.full_name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(user.media_count.toString(), "Beiträge")
                StatItem(user.follower_count.toString(), "Follower")
                StatItem(user.following_count.toString(), "Gefolgt")
            }
        }

        // Posts Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(posts) { post ->
                PostItem(post)
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PostItem(post: InstagramMedia) {
    val context = LocalContext.current
    val imageUrl = post.image_versions2?.candidates?.firstOrNull()?.url ?: ""
    
    Box(modifier = Modifier.aspectRatio(1f).clickable {
        // Start download for the post
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(
                DownloadWorker.KEY_URL to imageUrl,
                DownloadWorker.KEY_FILENAME to "insta_${post.id}.jpg",
                DownloadWorker.KEY_CATEGORY to "Posts"
            ))
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Simple overlay indicator for video or carousel
        if (post.media_type != 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )
            }
        }
    }
}