package com.example.postyapp.view

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.postyapp.model.model.Post
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream


@Composable
fun PostDialog(
    onDismiss: () -> Unit,
    onSubmit: (Post) -> Unit,
    post: Post? = null
) {
    var title by remember { mutableStateOf(post?.title ?: "") }
    var message by remember { mutableStateOf(post?.content ?: "") }
    var imagePath by remember { mutableStateOf(post?.photo ?: "") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = getPathFromUri(context, uri)
            if (path != null) {
                imagePath = path
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSubmit(Post(post?.id ?: 0, title, message, imagePath))
            }) {
                Text(if (post == null) "Create" else "Update")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(if (post == null) "New Post" else "Edit Post") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") })

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Choose Image")
                }

                if (imagePath.isNotEmpty()) {
                    Text("Selected image: $imagePath", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    )
}


fun getPathFromUri(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

