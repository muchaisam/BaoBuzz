package com.msdc.baobuzz

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.msdc.baobuzz.ux.OnboardingActivity
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.remove()
            }
        } else {
            setTheme(R.style.Theme_BaoBuzz)
        }

        // Immediately check for network connection
        if (!isNetworkConnected()) {
            // Show network alert dialog
            AlertDialog.Builder(this@MainActivity)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        } else {
            // Handle onboarding or home screen logic
            val sharedPref = getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
            val shown = sharedPref.getBoolean("Shown", false)

            if (!shown) {
                // Start OnboardingActivity
                val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
                startActivity(intent)
            } else {
                // Start HomeActivity
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
            }

            // Finish MainActivity to prevent back navigation to it
            finish()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork != null
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}