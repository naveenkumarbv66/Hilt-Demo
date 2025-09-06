package com.naveen.hiltdemo.data.repository

import com.naveen.hiltdemo.data.api.ApiService
import com.naveen.hiltdemo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    // Generic network call handler
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        NetworkResult.Success(body)
                    } ?: NetworkResult.Error("Empty response body")
                } else {
                    NetworkResult.Error(
                        message = response.message(),
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                NetworkResult.Error(
                    message = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    // GET operations
    suspend fun getUsers(): NetworkResult<List<User>> {
        return safeApiCall { apiService.getUsers() }
    }
    
    suspend fun getUserById(id: Int): NetworkResult<User> {
        return safeApiCall { apiService.getUserById(id) }
    }
    
    suspend fun getPosts(): NetworkResult<List<Post>> {
        return safeApiCall { apiService.getPosts() }
    }
    
    suspend fun getPostById(id: Int): NetworkResult<Post> {
        return safeApiCall { apiService.getPostById(id) }
    }
    
    suspend fun getPostsByUserId(userId: Int): NetworkResult<List<Post>> {
        return safeApiCall { apiService.getPostsByUserId(userId) }
    }
    
    // POST operations
    suspend fun createUser(user: CreateUserRequest): NetworkResult<User> {
        return safeApiCall { apiService.createUser(user) }
    }
    
    suspend fun createPost(post: CreatePostRequest): NetworkResult<Post> {
        return safeApiCall { apiService.createPost(post) }
    }
    
    // PUT operations
    suspend fun updateUser(id: Int, user: CreateUserRequest): NetworkResult<User> {
        return safeApiCall { apiService.updateUser(id, user) }
    }
    
    suspend fun updatePost(id: Int, post: CreatePostRequest): NetworkResult<Post> {
        return safeApiCall { apiService.updatePost(id, post) }
    }
    
    // DELETE operations
    suspend fun deleteUser(id: Int): NetworkResult<Unit> {
        return safeApiCall { apiService.deleteUser(id) }
    }
    
    suspend fun deletePost(id: Int): NetworkResult<Unit> {
        return safeApiCall { apiService.deletePost(id) }
    }
    
    // File upload operations
    suspend fun uploadImage(imageFile: File, description: String): NetworkResult<FileUploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = RequestBody.create(
                    "image/*".toMediaType(),
                    imageFile
                )
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestFile
                )
                val descriptionPart = RequestBody.create(
                    "text/plain".toMediaType(),
                    description
                )
                
                safeApiCall { 
                    apiService.uploadImage(imagePart, descriptionPart) 
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "File upload failed")
            }
        }
    }
    
    suspend fun uploadFile(file: File, description: String): NetworkResult<FileUploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = RequestBody.create(
                    "application/octet-stream".toMediaType(),
                    file
                )
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )
                val descriptionPart = RequestBody.create(
                    "text/plain".toMediaType(),
                    description
                )
                
                safeApiCall { 
                    apiService.uploadFile(filePart, descriptionPart) 
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "File upload failed")
            }
        }
    }
    
    // Generic data operations
    suspend fun getGenericData(): NetworkResult<ApiResponse<Any>> {
        return safeApiCall { apiService.getGenericData() }
    }
    
    suspend fun postGenericData(data: Map<String, Any>): NetworkResult<ApiResponse<Any>> {
        return safeApiCall { apiService.postGenericData(data) }
    }
    
    // Utility method for easy network calls from anywhere in the app
    suspend fun <T> makeNetworkCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return safeApiCall(apiCall)
    }
}
