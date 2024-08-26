package com.example.baobuzz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baobuzz.databinding.ItemLeagueBinding
import com.example.baobuzz.models.LeagueInfo

class LeagueAdapter : ListAdapter<LeagueInfo, LeagueAdapter.ViewHolder>(LeagueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeagueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemLeagueBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leagueInfo: LeagueInfo) {
            binding.apply {
                tvLeagueName.text = leagueInfo.name
                tvCountry.text = leagueInfo.country
                Glide.with(itemView.context)
                    .load(leagueInfo.flagUrl)
                    .into(ivLeagueLogo)
            }
        }
    }

    private class LeagueDiffCallback : DiffUtil.ItemCallback<LeagueInfo>() {
        override fun areItemsTheSame(oldItem: LeagueInfo, newItem: LeagueInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LeagueInfo, newItem: LeagueInfo): Boolean {
            return oldItem == newItem
        }
    }
}