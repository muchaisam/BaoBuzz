package com.example.baobuzz.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.daos.AppDatabase
import com.example.baobuzz.repository.StandingsRepository
import java.util.concurrent.TimeUnit

class StandingsUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private lateinit var appDatabase: AppDatabase

    override suspend fun doWork(): Result {

        val repository = StandingsRepository(ApiClient.footballApi, appDatabase)
        val topFiveLeagues = listOf(39, 140, 61, 78, 135)
        val currentSeason = 2024

        topFiveLeagues.forEach { leagueId ->
            try {
                repository.getStandings(leagueId, currentSeason)
            } catch (e: Exception) {
                // Log the error, but continue with other leagues
                e.printStackTrace()
            }
        }

        return Result.success()
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<StandingsUpdateWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "StandingsUpdate",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}