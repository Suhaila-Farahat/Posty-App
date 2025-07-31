package com.example.postyapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.postyapp.model.model.Post
import com.example.postyapp.viewmodel.PostViewModel
import kotlinx.coroutines.delay

@Composable
fun PostDetailScreen(
    post: Post,
    viewModel: PostViewModel,
    onBack: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    if (isDeleting) {
        LaunchedEffect(Unit) {
            viewModel.deletePost(post.id)
            delay(200)
            onBack()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        AsyncImage(
            model = post.photo,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(post.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(post.content, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = { showEditDialog = true }) {
                Text("Edit")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { isDeleting = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }

    if (showEditDialog) {
        PostDialog(
            post = post,
            onDismiss = { showEditDialog = false },
            onSubmit = {
                viewModel.updatePost(it)
                showEditDialog = false
                onBack()
            }
        )
    }
}
