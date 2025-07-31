package com.example.postyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postyapp.model.model.Post
import com.example.postyapp.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val allPosts = repository.getPosts()
                Log.d("PostViewModel", "Posts loaded: ${allPosts.size}")
                allPosts.forEachIndexed { index, post ->
                    Log.d("PostViewModel", "[$index] ID=${post.id}, Title=${post.title}, Content=${post.content}, Photo=${post.photo}")
                }
                _posts.value = allPosts
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("PostViewModel", "Failed to load posts", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addPost(post: Post) {
        viewModelScope.launch {
            try {
                repository.createPost(post)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("PostViewModel", "Create failed", e)
            }
        }
    }


    fun updatePost(post: Post) {
        viewModelScope.launch {
            repository.updatePost(post)
            loadPosts()
        }
    }

    fun deletePost(id: Int?) {
        if (id == null || id == 0) {
            _error.value = "Cannot delete: Invalid post ID"
            Log.e("PostViewModel", "Delete failed: invalid ID = $id")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("PostViewModel", "Calling deletePost($id)")
                val response = repository.deletePost(id)
                Log.d("PostViewModel", "Delete response: ${response.message}")
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("PostViewModel", "Delete failed", e)
            }
        }
    }

}
