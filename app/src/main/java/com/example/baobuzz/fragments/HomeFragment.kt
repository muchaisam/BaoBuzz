package com.example.baobuzz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baobuzz.adapter.LeagueAdapter
import com.example.baobuzz.databinding.FragmentHomeBinding
import com.example.baobuzz.models.HomeViewModel
import com.example.baobuzz.models.HomeViewModelFactory
import com.example.baobuzz.models.LeagueInfoProvider

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var leagueAdapter: LeagueAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, HomeViewModelFactory(LeagueInfoProvider()))
            .get(HomeViewModel::class.java)

        setupLeagueRecyclerView()
        observeLeagues()
    }

    private fun setupLeagueRecyclerView() {
        leagueAdapter = LeagueAdapter()
        binding.rvLeagues.apply {
            adapter = leagueAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeLeagues() {
        viewModel.leagues.observe(viewLifecycleOwner) { leagues ->
            leagueAdapter.submitList(leagues)
        }
    }
}

