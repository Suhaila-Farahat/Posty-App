package com.example.postyapp.model.repository

import android.util.Log
import com.example.postyapp.model.model.DeleteResponse
import com.example.postyapp.model.model.Post
import com.example.postyapp.model.model.PostEditRequest
import com.example.postyapp.model.remoteDataSource.PostApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileNotFoundException

class PostRepository(private val api: PostApiService) {

    suspend fun getPosts() = api.getPosts()

    suspend fun createPost(post: Post) {
        val file = File(post.photo)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

        val titlePart = post.title.toRequestBody("text/plain".toMediaType())
        val contentPart = post.content.toRequestBody("text/plain".toMediaType())

        val response = api.createPost(titlePart, contentPart, photoPart)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("Create failed: ${response.code()} $errorBody")
        }
    }

    suspend fun updatePost(post: Post) {
        val id = post.id ?: throw IllegalArgumentException("Post ID is required")

        val isLocalImage = !(post.photo.startsWith("http://") || post.photo.startsWith("https://"))
        val titlePart = post.title.toRequestBody("text/plain".toMediaType())
        val contentPart = post.content.toRequestBody("text/plain".toMediaType())

        val response = if (isLocalImage) {
            try {
                val file = File(post.photo)
                if (!file.exists()) throw FileNotFoundException("Image file not found at ${post.photo}")

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                api.updatePost(id, titlePart, contentPart, photoPart)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid photo selected. Please choose another image.")
            }
        } else {
            api.updatePostWithoutImage(id, PostEditRequest(post.title, post.content))
        }

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("Update failed: ${response.code()} $errorBody")
        }
    }


    suspend fun deletePost(id: Int): DeleteResponse {
        val response = api.deletePost(id)
        return response
    }
}
