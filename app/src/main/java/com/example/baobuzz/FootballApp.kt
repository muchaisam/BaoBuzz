package com.example.baobuzz

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import de.hdodenhof.circleimageview.BuildConfig
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FootballApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            // Log to crash reporting service
            // Example: FirebaseCrashlytics.getInstance().log(message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    // Example: FirebaseCrashlytics.getInstance().recordException(t)
                } else if (priority == Log.WARN) {
                    // Example: FirebaseCrashlytics.getInstance().recordException(t)
                }
            }
        }
    }
}