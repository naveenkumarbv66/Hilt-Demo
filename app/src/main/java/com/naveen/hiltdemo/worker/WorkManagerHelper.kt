package com.naveen.hiltdemo.worker

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleGetUsersWork() {
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInputData(workDataOf("operation" to "get_users"))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    fun scheduleGetPostsWork() {
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInputData(workDataOf("operation" to "get_posts"))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    fun scheduleCreateUserWork(name: String, email: String, phone: String) {
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
    }
    
    fun scheduleCreatePostWork(title: String, body: String, userId: Int, image: String? = null, file: String? = null) {
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
    
    fun cancelAllWork() {
        workManager.cancelAllWork()
    }
    
    fun getWorkInfoById(workId: String) = workManager.getWorkInfoById(UUID.fromString(workId))
}
