package com.example.postyapp.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.postyapp.model.model.Post
import com.example.postyapp.viewmodel.PostViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Post")
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text("Error: $error", modifier = Modifier.align(Alignment.Center))
                    Log.e("HomeScreen", "UI Error: $error")


                }
                else -> {
                    LazyColumn {
                        items(posts) { post ->
                            PostItem(post = post, onClick = { onPostClick(post) })
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
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = post.photo,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(post.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
