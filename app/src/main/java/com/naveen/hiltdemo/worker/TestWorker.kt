package com.naveen.hiltdemo.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TestWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Result.success(
                workDataOf(
                    "message" to "Test worker executed successfully",
                    "timestamp" to System.currentTimeMillis().toString()
                )
            )
        } catch (e: Exception) {
            Result.failure(
                workDataOf(
                    "error" to (e.message ?: "Unknown error")
                )
            )
        }
    }
}
