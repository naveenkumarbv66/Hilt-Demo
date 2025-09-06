package com.naveen.hiltdemo.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
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
                    when (result) {
                        is com.naveen.hiltdemo.data.model.NetworkResult.Success -> {
                            Result.success(
                                workDataOf(
                                    "operation" to operation,
                                    "success_message" to "Users loaded successfully (${result.data.size} users)",
                                    "data_count" to result.data.size
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Error -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to result.message,
                                    "error_code" to result.code
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Loading -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to "Unexpected loading state"
                                )
                            )
                        }
                    }
                }
                "get_posts" -> {
                    val result = networkRepository.getPosts()
                    when (result) {
                        is com.naveen.hiltdemo.data.model.NetworkResult.Success -> {
                            Result.success(
                                workDataOf(
                                    "operation" to operation,
                                    "success_message" to "Posts loaded successfully (${result.data.size} posts)",
                                    "data_count" to result.data.size
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Error -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to result.message,
                                    "error_code" to result.code
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Loading -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to "Unexpected loading state"
                                )
                            )
                        }
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
                    when (result) {
                        is com.naveen.hiltdemo.data.model.NetworkResult.Success -> {
                            Result.success(
                                workDataOf(
                                    "operation" to operation,
                                    "success_message" to "User created successfully: ${result.data.name}",
                                    "user_name" to result.data.name,
                                    "user_email" to result.data.email
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Error -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to result.message,
                                    "error_code" to result.code
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Loading -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to "Unexpected loading state"
                                )
                            )
                        }
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
                    when (result) {
                        is com.naveen.hiltdemo.data.model.NetworkResult.Success -> {
                            Result.success(
                                workDataOf(
                                    "operation" to operation,
                                    "success_message" to "Post created successfully: ${result.data.title}",
                                    "post_title" to result.data.title,
                                    "post_id" to result.data.id
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Error -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to result.message,
                                    "error_code" to result.code
                                )
                            )
                        }
                        is com.naveen.hiltdemo.data.model.NetworkResult.Loading -> {
                            Result.failure(
                                workDataOf(
                                    "operation" to operation,
                                    "error_message" to "Unexpected loading state"
                                )
                            )
                        }
                    }
                }
                else -> {
                    Result.failure(
                        workDataOf(
                            "operation" to operation,
                            "error_message" to "Unknown operation: $operation"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                workDataOf(
                    "operation" to (inputData.getString("operation") ?: "unknown"),
                    "error_message" to (e.message ?: "Unknown exception occurred"),
                    "exception_type" to e.javaClass.simpleName
                )
            )
        }
    }
}
