package com.example.baobuzz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.baobuzz.R
import com.example.baobuzz.databinding.ItemMatchBinding
import com.example.baobuzz.models.Fixture
import java.text.SimpleDateFormat
import java.util.Locale

class MatchAdapter(private var matches: List<Fixture>) :
    RecyclerView.Adapter<MatchAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fixture: Fixture) {
            binding.apply {
                tvHomeTeam.text = fixture.teams.home.name
                tvAwayTeam.text = fixture.teams.away.name
                tvMatchDate.text = formatDate(fixture.fixture.date)
                tvMatchTime.text = formatTime(fixture.fixture.date)
                tvMatchStatus.text = fixture.fixture.status.short

                // Load team logos using an image loading library like Coil
                ivHomeTeam.load(fixture.teams.home.logo) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_home)
                }
                ivAwayTeam.load(fixture.teams.away.logo) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_away)
                }
            }

            binding.tvMatchDate.text = formatDate(fixture.fixture.date)
            binding.tvMatchTime.text = formatTime(fixture.fixture.date)
        }


        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date)
        }

        private fun formatTime(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount() = matches.size

    fun updateMatches(newMatches: List<Fixture>) {
        matches = newMatches
        notifyDataSetChanged()
    }
}