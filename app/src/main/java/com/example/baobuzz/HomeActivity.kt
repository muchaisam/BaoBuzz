package com.example.baobuzz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.baobuzz.fragments.StatisticsFragment
import com.example.baobuzz.workmanager.FixtureWorker
import com.example.baobuzz.workmanager.StandingsUpdateWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    // Variable to track if the back button was pressed twice
    private var doubleBackToExitPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set the theme based on system settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Set up the navigation components
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNav, navController)

        // Handle the back button press to exit the app
        onBackPressedDispatcher.addCallback(this@HomeActivity) {
            if (doubleBackToExitPressed) {
                finish()
            } else {
                doubleBackToExitPressed = true
                Toast.makeText(this@HomeActivity, "Tap again to exit", Toast.LENGTH_SHORT)
                    .show()
                Handler(Looper.getMainLooper()).postDelayed(
                    { doubleBackToExitPressed = false },
                    1000
                )
            }
        }

        // Schedule the background update task
        StandingsUpdateWorker.schedule(applicationContext)

        FixtureWorker.schedule(this)
//        scheduleLiveScoreUpdates()
    }

//    fun scheduleLiveScoreUpdates() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val repeatingRequest = PeriodicWorkRequestBuilder<LiveScoreUpdateWorker>(15, TimeUnit.MINUTES)
//            .setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
//            "live_score_updates",
//            ExistingPeriodicWorkPolicy.REPLACE,
//            repeatingRequest
//        )
//    }

}