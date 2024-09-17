package com.msdc.baobuzz.workmanager

import android.content.Context
import android.os.PowerManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.msdc.baobuzz.repository.FootballRepository
import java.util.concurrent.TimeUnit
import androidx.work.WorkManager

class FixtureWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: FootballRepository,
    private val userActivityTracker: UserActivityTracker
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            if (userActivityTracker.isUserActive()) {
                val leagueIds = listOf(39, 140, 61, 78, 135) // Top 5 leagues
                leagueIds.forEach { leagueId ->
                    repository.fetchAndCacheFixtures(leagueId, 20)
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "FixtureUpdateWork"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<FixtureWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}

class UserActivityTracker(private val context: Context) {
    fun isUserActive(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }
}