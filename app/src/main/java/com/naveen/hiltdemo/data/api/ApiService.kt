package com.naveen.hiltdemo.data.api

import com.naveen.hiltdemo.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // GET endpoints
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>
    
    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>
    
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Response<Post>
    
    @GET("posts")
    suspend fun getPostsByUserId(@Query("userId") userId: Int): Response<List<Post>>
    
    // POST endpoints
    @POST("users")
    suspend fun createUser(@Body user: CreateUserRequest): Response<User>
    
    @POST("posts")
    suspend fun createPost(@Body post: CreatePostRequest): Response<Post>
    
    // PUT endpoints
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: CreateUserRequest): Response<User>
    
    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: CreatePostRequest): Response<Post>
    
    // DELETE endpoints
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
    
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>
    
    // File upload endpoints
    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<FileUploadResponse>
    
    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<FileUploadResponse>
    
    // Generic API response endpoints
    @GET("api/data")
    suspend fun getGenericData(): Response<ApiResponse<Any>>
    
    @POST("api/data")
    suspend fun postGenericData(@Body data: Map<String, Any>): Response<ApiResponse<Any>>
}
