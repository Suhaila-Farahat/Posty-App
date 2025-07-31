package com.example.postyapp.view

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.postyapp.model.model.Post
import com.example.postyapp.viewmodel.PostViewModel

@Composable
fun HomeScreen(viewModel: PostViewModel, onPostClick: (Post) -> Unit) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    var refresh by remember { mutableStateOf(false) }

    LaunchedEffect(refresh) {
        viewModel.loadPosts()
    }

    DisposableEffect(Unit) {
        onDispose {
            refresh = !refresh
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("New Post") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)

        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Oops!", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "We couldn't connect to the server. Please check your internet connection and try again",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPosts() }) {
                            Text("Try Again")
                        }
                    }
                }

                else -> {
                    if (posts.isEmpty()) {
                        Text(
                            text = "No posts available. Click the + button to create one!",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(posts) { index, post ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it * (index + 1) / 4 }),
                                ) {
                                    PostItem(post = post, onClick = { onPostClick(post) })
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            PostDialog(
                onDismiss = { showDialog = false },
                onSubmit = {
                    viewModel.addPost(it)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = post.photo,
                contentDescription = "Post Image",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to view details",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
