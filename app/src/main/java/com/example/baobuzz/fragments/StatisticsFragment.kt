package com.example.baobuzz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.daos.AppDatabase
import com.example.baobuzz.models.StandingsViewModel
import com.example.baobuzz.repository.StandingsRepository
import com.example.baobuzz.components.StandingsScreen


class StatisticsFragment : Fragment() {
    private lateinit var viewModel: StandingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val repository = StandingsRepository(ApiClient.footballApi,
                AppDatabase.getInstance(requireContext()))
        val factory = StandingsViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[StandingsViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                val darkTheme = isSystemInDarkTheme()
                MaterialTheme(
                    colors = if (darkTheme) darkColors() else lightColors()
                ) {
                    StandingsScreen(viewModel)
                }
            }
        }
    }
}