package com.msdc.baobuzz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.msdc.baobuzz.adapter.LeagueAdapter
import com.msdc.baobuzz.adapter.TransfersAdapter
import com.msdc.baobuzz.api.ApiClient
import com.msdc.baobuzz.components.CoachCareerViewer
import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.daos.Transfer
import com.msdc.baobuzz.databinding.FragmentHomeBinding
import com.msdc.baobuzz.interfaces.NetworkResult
import com.msdc.baobuzz.models.CoachViewModel
import com.msdc.baobuzz.models.CoachViewModelFactory
import com.msdc.baobuzz.models.HomeViewModel
import com.msdc.baobuzz.models.HomeViewModelFactory
import com.msdc.baobuzz.models.LeagueInfoProvider
import com.msdc.baobuzz.models.TeamConfig
import com.msdc.baobuzz.models.TeamConfigManager
import com.msdc.baobuzz.models.TransfersViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: CoachViewModelFactory

    private val viewModel: CoachViewModel by viewModels { viewModelFactory }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var leagueAdapter: LeagueAdapter
    private val transfersViewModel: TransfersViewModel by viewModels()
    private val adapter = TransfersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set up ComposeView to display CoachScreen
        val composeView = binding.composeView
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            CoachCareerViewer(viewModel = viewModel)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val footballApi = ApiClient.footballApi
        val db = AppDatabase.getInstance(requireContext())
        val viewModelFactory = HomeViewModelFactory(LeagueInfoProvider())
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]


        setupLeagueRecyclerView()
        observeLeagues()


        Timber.d("CombinedFragment: onViewCreated")

        setupRecyclerView()
        setupTeamChips()
        observeTransfers()
        setupRetryButton()

        // Load transfers for the first team by default
        transfersViewModel.selectTeam(TeamConfigManager.getTeams().first())

        viewModel.loadCoaches(listOf(4, 10, 11))
    }


    private fun setupLeagueRecyclerView() {
        leagueAdapter = LeagueAdapter()
        binding.rvLeagues.apply {
            adapter = leagueAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeLeagues() {
        homeViewModel.leagues.observe(viewLifecycleOwner) { leagues ->
            leagueAdapter.submitList(leagues)
        }
    }


    private fun setupRecyclerView() {
        binding.transfersRecyclerView.adapter = adapter
        binding.transfersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupTeamChips() {
        TeamConfigManager.getTeams().forEach { team ->
            val chip = Chip(requireContext()).apply {
                text = team.name
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) transfersViewModel.selectTeam(team)
                }
            }
            binding.teamChipGroup.addView(chip)
        }
    }

    private fun observeTransfers() {
        viewLifecycleOwner.lifecycleScope.launch {
            transfersViewModel.transfersState.collect { state ->
                updateUiState(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            transfersViewModel.selectedTeam.collect { team ->
                team?.let {
                    updateSelectedTeamChip(it)
                    transfersViewModel.transfersState.value[it.id]?.let { result ->
                        when (result) {
                            is NetworkResult.Success -> showTransfers(result.data)
                            is NetworkResult.Error -> showError(result.message)
                            is NetworkResult.Loading -> showLoading()
                        }
                    }
                }
            }
        }
    }

    private fun updateUiState(state: Map<Int, NetworkResult<List<Transfer>>>) {
        val allLoading = state.values.all { it is NetworkResult.Loading }
        val anyError = state.values.any { it is NetworkResult.Error }

        when {
            allLoading -> showLoading()
            anyError -> {
                val errorMessage = state.values.filterIsInstance<NetworkResult.Error>().firstOrNull()?.message
                    ?: "An error occurred"
                showError(errorMessage)
            }
            else -> transfersViewModel.selectedTeam.value?.let { selectedTeam ->
                state[selectedTeam.id]?.let { result ->
                    when (result) {
                        is NetworkResult.Success -> showTransfers(result.data)
                        is NetworkResult.Error -> showError(result.message)
                        is NetworkResult.Loading -> showLoading()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            transfersRecyclerView.visibility = View.GONE
            errorLayout.visibility = View.GONE
        }
    }

    private fun showTransfers(transfers: List<Transfer>) {
        binding.apply {
            progressBar.visibility = View.GONE
            transfersRecyclerView.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
        }
        adapter.submitList(transfers)
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            transfersRecyclerView.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            errorMessage.text = message
        }
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            transfersViewModel.retry()
        }
    }



    private fun updateSelectedTeamChip(selectedTeam: TeamConfig) {
        binding.teamChipGroup.children.filterIsInstance<Chip>().forEach { chip ->
            chip.isChecked = (chip.text.toString() == selectedTeam.name)
        }
    }
}


