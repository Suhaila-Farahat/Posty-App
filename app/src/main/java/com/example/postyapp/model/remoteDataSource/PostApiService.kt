package com.example.postyapp.model.remoteDataSource

import com.example.postyapp.model.model.DeleteResponse
import com.example.postyapp.model.model.Post
import com.example.postyapp.model.model.PostEditRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PostApiService {
    @GET("blogs")
    suspend fun getPosts(): List<Post>

    @Headers("Accept: application/json")
    @Multipart
    @POST("blogs/store")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<ResponseBody>



    @Multipart
    @POST("blogs/update/{id}")
    suspend fun updatePost(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("blogs/update/{id}")
    suspend fun updatePostWithoutImage(
        @Path("id") id: Int,
        @Body post: PostEditRequest
    ): Response<ResponseBody>


    @POST("blogs/delete/{id}")
    suspend fun deletePost(@Path("id") id: Int): DeleteResponse

}


