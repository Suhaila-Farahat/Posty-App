package com.example.postyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.postyapp.model.model.Post
import com.example.postyapp.model.remoteDataSource.PostApiService
import com.example.postyapp.model.repository.PostRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.postyapp.view.HomeScreen
import com.example.postyapp.view.PostDetailScreen
import com.example.postyapp.viewmodel.PostViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://taskapi.astra-tech.net/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()




        val api = retrofit.create(PostApiService::class.java)
        val repository = PostRepository(api)

        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PostViewModel(repository) as T
                }
            }
        )[PostViewModel::class.java]

        setContent {
            MaterialTheme {
                var selectedPost by remember { mutableStateOf<Post?>(null) }

                if (selectedPost == null) {
                    HomeScreen(viewModel) { post -> selectedPost = post }
                } else {
                    PostDetailScreen(
                        post = selectedPost!!,
                        viewModel = viewModel,
                        onBack = { selectedPost = null }
                    )
                }
            }
        }
    }
}
