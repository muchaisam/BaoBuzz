package com.msdc.baobuzz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msdc.baobuzz.R
import com.msdc.baobuzz.databinding.ItemMatchBinding
import com.msdc.baobuzz.models.Fixture
import java.text.SimpleDateFormat
import java.util.Locale

class MatchAdapter : ListAdapter<Fixture, MatchAdapter.ViewHolder>(FixtureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fixture: Fixture) {
            binding.apply {
                tvHomeTeam.text = fixture.teams.home.name
                tvAwayTeam.text = fixture.teams.away.name
                tvMatchDate.text = formatDate(fixture.fixture.date)
                tvMatchTime.text = formatTime(fixture.fixture.date)
                tvMatchStatus.text = fixture.fixture.status.short

                Glide.with(itemView.context)
                    .load(fixture.teams.home.logo)
                    .placeholder(R.drawable.placeholder_home)
                    .into(ivHomeTeam)

                Glide.with(itemView.context)
                    .load(fixture.teams.away.logo)
                    .placeholder(R.drawable.placeholder_away)
                    .into(ivAwayTeam)
            }
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

    private class FixtureDiffCallback : DiffUtil.ItemCallback<Fixture>() {
        override fun areItemsTheSame(oldItem: Fixture, newItem: Fixture): Boolean {
            return oldItem.fixture.id == newItem.fixture.id
        }

        override fun areContentsTheSame(oldItem: Fixture, newItem: Fixture): Boolean {
            return oldItem == newItem
        }
    }
}