package com.example.baobuzz.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.baobuzz.R
import com.example.baobuzz.adapter.MatchAdapter
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.cache.ImageLoader
import com.example.baobuzz.daos.AppDatabase
import com.example.baobuzz.databinding.FragmentCalendarBinding
import com.example.baobuzz.models.CalendarViewModel
import com.example.baobuzz.repository.FootballRepository
import com.google.android.material.chip.Chip
import com.example.baobuzz.interfaces.Result
import com.example.baobuzz.models.LeagueInfoProvider
import com.example.baobuzz.ux.BlurredProgressDialog


class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var imageLoader: ImageLoader
    private lateinit var viewModel: CalendarViewModel
    private lateinit var progressDialog: BlurredProgressDialog
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = FootballRepository(ApiClient.footballApi, AppDatabase.getInstance(requireContext()))
        val leagueInfoProvider = LeagueInfoProvider()
        val factory = CalendarViewModel.Factory(repository, leagueInfoProvider)
        viewModel = ViewModelProvider(this, factory)[CalendarViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = FootballRepository(ApiClient.footballApi, AppDatabase.getInstance(requireContext()))
        val leagueInfoProvider = LeagueInfoProvider()
        val factory = CalendarViewModel.Factory(repository, leagueInfoProvider)
        viewModel = ViewModelProvider(this, factory)[CalendarViewModel::class.java]

        setupRecyclerView()
        setupChips()
        initializeComponents()
        observeViewModel()
    }

    private fun initializeComponents() {
        progressDialog = BlurredProgressDialog(requireContext(), R.style.CustomProgressDialogTheme)
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter()
        binding.rvFixtures.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchAdapter
        }
    }

    private fun setupChips() {
        val leagueInfoList = viewModel.getLeagueInfo()
        binding.chipGroup.removeAllViews() // Clear existing chips
        leagueInfoList.forEach { leagueInfo ->
            val chip = Chip(context).apply {
                text = leagueInfo.name
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.selectLeague(leagueInfo.id)
                    }
                }
            }

            // Load league logo
            Glide.with(this)
                .load(leagueInfo.flagUrl)
                .circleCrop()
                .into(object : CustomTarget<Drawable>(24, 24) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        chip.chipIcon = resource
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        chip.chipIcon = null
                    }
                })

            binding.chipGroup.addView(chip)
        }
    }


    private fun observeViewModel() {
        viewModel.fixtures.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    progressDialog.dismiss()
                    matchAdapter.submitList(result.data)
                    if (result.data.isEmpty()) {
                        binding.tvNoMatches.visibility = View.VISIBLE
                    } else {
                        binding.tvNoMatches.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    progressDialog.dismiss()
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "Error: ${result.exception.message}"
                }
                is Result.Loading -> {
                    progressDialog.show()
                    binding.tvError.visibility = View.GONE
                    binding.tvNoMatches.visibility = View.GONE
                }
            }
        }

        viewModel.selectedLeagueId.observe(viewLifecycleOwner) { leagueId ->
            val selectedLeagueName = viewModel.getLeagueInfo().find { it.id == leagueId }?.name
            binding.chipGroup.children.filterIsInstance<Chip>().forEach { chip ->
                chip.isChecked = (chip.text.toString() == selectedLeagueName)
            }
        }
    }
}