package com.naveen.hiltdemo.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.naveen.hiltdemo.data.repository.NetworkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NetworkWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val operation = inputData.getString("operation") ?: "get_users"
            
            when (operation) {
                "get_users" -> {
                    val result = networkRepository.getUsers()
                    if (result is com.naveen.hiltdemo.data.model.NetworkResult.Success) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
                "get_posts" -> {
                    val result = networkRepository.getPosts()
                    if (result is com.naveen.hiltdemo.data.model.NetworkResult.Success) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
                "create_user" -> {
                    val name = inputData.getString("name") ?: "Test User"
                    val email = inputData.getString("email") ?: "test@example.com"
                    val phone = inputData.getString("phone") ?: "1234567890"
                    
                    val userRequest = com.naveen.hiltdemo.data.model.CreateUserRequest(
                        name = name,
                        email = email,
                        phone = phone
                    )
                    
                    val result = networkRepository.createUser(userRequest)
                    if (result is com.naveen.hiltdemo.data.model.NetworkResult.Success) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
                "create_post" -> {
                    val title = inputData.getString("title") ?: "Test Post"
                    val body = inputData.getString("body") ?: "Test post body"
                    val userId = inputData.getInt("userId", 1)
                    val image = inputData.getString("image")
                    val file = inputData.getString("file")
                    
                    val postRequest = com.naveen.hiltdemo.data.model.CreatePostRequest(
                        title = title,
                        body = body,
                        userId = userId,
                        image = image,
                        file = file
                    )
                    
                    val result = networkRepository.createPost(postRequest)
                    if (result is com.naveen.hiltdemo.data.model.NetworkResult.Success) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
                else -> Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
