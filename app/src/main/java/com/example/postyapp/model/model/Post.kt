package com.example.postyapp.model.model

data class Post(
    val id: Int?,
    val title: String,
    val content: String,
    val photo: String,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class PostCreateRequest(
    val title: String,
    val content: String,
    val photo: String
)

data class PostEditRequest(
    val title: String,
    val content: String
)

data class DeleteResponse(
    val message: String
)

