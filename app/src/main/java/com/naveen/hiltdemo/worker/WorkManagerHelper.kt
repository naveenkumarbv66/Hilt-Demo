package com.naveen.hiltdemo.worker

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleGetUsersWork(): UUID {
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInputData(workDataOf("operation" to "get_users"))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(workRequest)
        return workRequest.id
    }
    
    fun scheduleGetPostsWork(): UUID {
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInputData(workDataOf("operation" to "get_posts"))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(workRequest)
        return workRequest.id
    }
    
    fun scheduleCreateUserWork(name: String, email: String, phone: String): UUID {
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInputData(
                workDataOf(
                    "operation" to "create_user",
                    "name" to name,
                    "email" to email,
                    "phone" to phone
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(workRequest)
        return workRequest.id
    }
    
    fun scheduleCreatePostWork(title: String, body: String, userId: Int, image: String? = null, file: String? = null): UUID {
        val inputDataBuilder = Data.Builder()
            .putString("operation", "create_post")
            .putString("title", title)
            .putString("body", body)
            .putInt("userId", userId)
        
        // Add optional image and file parameters if provided
        if (image != null) {
            inputDataBuilder.putString("image", image)
        }
        if (file != null) {
            inputDataBuilder.putString("file", file)
        }
        
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInputData(inputDataBuilder.build())
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(workRequest)
        return workRequest.id
    }
    
    fun schedulePeriodicSyncWork() {
        val workRequest = PeriodicWorkRequestBuilder<NetworkWorker>(
            15, TimeUnit.MINUTES
        )
            .setInputData(workDataOf("operation" to "get_users"))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    
    fun scheduleTestWork(): UUID {
        val workRequest = OneTimeWorkRequestBuilder<TestWorker>()
            .setInputData(workDataOf("test" to "simple_test"))
            .build()
        
        workManager.enqueue(workRequest)
        return workRequest.id
    }
    
    fun cancelAllWork() {
        workManager.cancelAllWork()
    }
    
    fun cancelWork(workId: UUID) {
        workManager.cancelWorkById(workId)
    }
    
    fun getWorkInfoById(workId: String) = workManager.getWorkInfoById(UUID.fromString(workId))
    
    // Observe work result
    fun observeWorkResult(workId: UUID): Flow<WorkInfo> {
        return workManager.getWorkInfoByIdFlow(workId)
            .flowOn(Dispatchers.IO)
    }
    
    // Observe work result with custom data extraction
    fun observeWorkResultWithData(workId: UUID): Flow<WorkResult> {
        return flow {
            workManager.getWorkInfoByIdFlow(workId)
                .collect { workInfo ->
                    val result = when {
                        workInfo.state == WorkInfo.State.SUCCEEDED -> {
                            val outputData = workInfo.outputData
                            val operation = outputData.getString("operation") ?: "unknown"
                            val successMessage = outputData.getString("success_message") ?: "Operation completed successfully"
                            WorkResult.Success(operation, successMessage, outputData)
                        }
                        workInfo.state == WorkInfo.State.FAILED -> {
                            val outputData = workInfo.outputData
                            val operation = outputData.getString("operation") ?: "unknown"
                            val errorMessage = outputData.getString("error_message") ?: "Operation failed"
                            WorkResult.Error(operation, errorMessage)
                        }
                        workInfo.state == WorkInfo.State.CANCELLED -> {
                            WorkResult.Cancelled("Work was cancelled")
                        }
                        workInfo.state == WorkInfo.State.RUNNING -> {
                            WorkResult.Running("Work is running...")
                        }
                        workInfo.state == WorkInfo.State.ENQUEUED -> {
                            WorkResult.Queued("Work is queued")
                        }
                        workInfo.state == WorkInfo.State.BLOCKED -> {
                            WorkResult.Blocked("Work is blocked")
                        }
                        else -> {
                            WorkResult.Unknown("Unknown work state: ${workInfo.state}")
                        }
                    }
                    emit(result)
                }
        }.flowOn(Dispatchers.IO)
    }
}

// Work result sealed class
sealed class WorkResult {
    data class Success(val operation: String, val message: String, val data: Data) : WorkResult()
    data class Error(val operation: String, val message: String) : WorkResult()
    data class Cancelled(val message: String) : WorkResult()
    data class Running(val message: String) : WorkResult()
    data class Queued(val message: String) : WorkResult()
    data class Blocked(val message: String) : WorkResult()
    data class Unknown(val message: String) : WorkResult()
}
