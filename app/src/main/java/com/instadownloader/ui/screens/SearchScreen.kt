package com.instadownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.instadownloader.data.local.SearchHistoryEntity
import com.instadownloader.data.model.Highlight
import com.instadownloader.data.model.InstagramMedia
import com.instadownloader.data.model.InstagramUser
import com.instadownloader.ui.components.GlassCard
import com.instadownloader.ui.components.StoryRingAvatar
import com.instadownloader.ui.viewmodel.SearchUiState
import com.instadownloader.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialUrl: String? = null,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf(initialUrl ?: "") }
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.searchHistory.collectAsState(initial = emptyList())

    LaunchedEffect(initialUrl) {
        if (initialUrl != null) {
            viewModel.searchByUrl(initialUrl)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                UserDetailContent(
                    user = state.user,
                    posts = state.posts,
                    stories = state.stories,
                    highlights = state.highlights,
                    reels = state.reels,
                    saved = state.saved,
                    tagged = state.tagged,
                    archive = state.archive,
                    onHighlightClick = { viewModel.downloadHighlight(it, state.user.username) },
                    onDownloadClick = { url, filename -> viewModel.enqueueDownload(url, filename, state.user.username) }
                )
            }
            is SearchUiState.SuccessMedia -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Vorschau", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                        PostItem(
                            post = state.media,
                            onDownloadClick = { url, filename -> 
                                viewModel.enqueueDownload(url, filename, state.media.user?.username ?: "Downloads") 
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Klicke auf das Bild zum Herunterladen", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
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
                        modifier = Modifier.size(40.dp).clip(CircleShape),
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
fun UserDetailContent(
    user: InstagramUser,
    posts: List<InstagramMedia>,
    stories: List<InstagramMedia>,
    highlights: List<Highlight>,
    reels: List<InstagramMedia>,
    saved: List<InstagramMedia>,
    tagged: List<InstagramMedia>,
    archive: List<InstagramMedia>,
    onHighlightClick: (Highlight) -> Unit,
    onDownloadClick: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GlassCard(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clickable {
                    val hdPicUrl = user.profile_pic_url_hd.ifEmpty { user.profile_pic_url }
                    if (hdPicUrl.isNotEmpty()) {
                        onDownloadClick(hdPicUrl, "profile_${user.username}.jpg")
                    }
                }) {
                    StoryRingAvatar(imageUrl = user.profile_pic_url, hasStory = stories.isNotEmpty(), size = 64.dp)
                }
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

        if (stories.isNotEmpty()) {
            Text("Stories", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(stories) { story ->
                    Box(modifier = Modifier.size(70.dp).clickable {
                        val url = story.video_versions?.firstOrNull()?.url ?: story.image_versions2?.candidates?.firstOrNull()?.url ?: ""
                        val ext = if (story.media_type == 2) "mp4" else "jpg"
                        onDownloadClick(url, "story_${story.id}.$ext")
                    }) {
                        StoryRingAvatar(
                            imageUrl = story.image_versions2?.candidates?.lastOrNull()?.url ?: "",
                            hasStory = true,
                            size = 60.dp
                        )
                    }
                }
            }
        }

        if (highlights.isNotEmpty()) {
            Text("Highlights", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(highlights) { highlight ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(70.dp).clickable { onHighlightClick(highlight) }
                    ) {
                        AsyncImage(
                            model = highlight.cover_media?.cropped_image_version?.url,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            highlight.title,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        var selectedProfileTab by remember { mutableStateOf(0) }
        val profileTabs = listOf("Beiträge", "Reels", "Markiert", "Gespeichert", "Archiv")

        ScrollableTabRow(
            selectedTabIndex = selectedProfileTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp
        ) {
            profileTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedProfileTab == index,
                    onClick = { selectedProfileTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val activeMediaList = when (selectedProfileTab) {
            0 -> posts
            1 -> reels
            2 -> tagged
            3 -> saved
            4 -> archive
            else -> emptyList()
        }

        if (activeMediaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("Keine Medien gefunden", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(activeMediaList) { media ->
                    PostItem(media, onDownloadClick)
                }
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
fun PostItem(
    post: InstagramMedia,
    onDownloadClick: (String, String) -> Unit
) {
    val imageUrl = post.image_versions2?.candidates?.firstOrNull()?.url ?: 
                   post.carousel_media?.firstOrNull()?.image_versions2?.candidates?.firstOrNull()?.url ?: ""
    
    Box(modifier = Modifier.aspectRatio(1f).clickable {
        when (post.media_type) {
            1 -> { // Image
                val url = post.image_versions2?.candidates?.firstOrNull()?.url ?: return@clickable
                onDownloadClick(url, "img_${post.id}.jpg")
            }
            2 -> { // Video
                val url = post.video_versions?.firstOrNull()?.url ?: return@clickable
                onDownloadClick(url, "vid_${post.id}.mp4")
            }
            8 -> { // Carousel
                post.carousel_media?.forEachIndexed { index, media ->
                    if (media.media_type == 1) {
                        val url = media.image_versions2?.candidates?.firstOrNull()?.url ?: return@forEachIndexed
                        onDownloadClick(url, "car_${post.id}_$index.jpg")
                    } else if (media.media_type == 2) {
                        val url = media.video_versions?.firstOrNull()?.url ?: return@forEachIndexed
                        onDownloadClick(url, "car_${post.id}_$index.mp4")
                    }
                }
            }
        }
    }) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (post.media_type != 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                Icon(
                    if (post.media_type == 8) Icons.Default.PhotoLibrary else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }
    }
}