package com.msdc.baobuzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.msdc.baobuzz.core.navigation.BaoBuzzNavigation
import com.msdc.baobuzz.ui.theme.BaoBuzzTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            BaoBuzzTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    BaoBuzzNavigation()
                }
            }
        }
    }
}