package com.msdc.baobuzz.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msdc.baobuzz.repository.CoachRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CoachUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CoachRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val coaches = repository.getAllCachedCoaches()
            coaches.forEach { coach ->
                repository.getCoach(coach.id)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}