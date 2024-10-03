package com.msdc.baobuzz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.msdc.baobuzz.HomeActivity
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeagueSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeagueSelectionScreenWrapper()
        }
    }

    @Composable
    fun LeagueSelectionScreenWrapper() {
        val viewModel: LeagueSelectionViewModel = hiltViewModel()
        LeagueSelectionScreen(
            viewModel = viewModel,
            onComplete = {
                startActivity(Intent(this@LeagueSelectionActivity, HomeActivity::class.java))
                finish()
            },
            reduceMotion = false // You may want to make this configurable
        )
    }
}