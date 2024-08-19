package com.example.baobuzz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.baobuzz.R
import com.example.baobuzz.databinding.ItemsLeagueBinding
import com.example.baobuzz.models.Fixture
import com.example.baobuzz.models.League
import com.example.baobuzz.models.LeagueWithFixtures

class LeagueAdapter : RecyclerView.Adapter<LeagueAdapter.ViewHolder>() {

    private var leagues: List<LeagueWithFixtures> = emptyList()

    inner class ViewHolder(private val binding: ItemsLeagueBinding) : RecyclerView.ViewHolder(binding.root) {

        private val matchAdapter = MatchAdapter(emptyList())

        init {
            binding.rvMatches.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = matchAdapter
            }
        }

        fun bind(league: LeagueWithFixtures) {
            binding.apply {
                tvLeagueName.text = league.name
                // Load league flag using an image loading library like Coil
                ivLeagueFlag.load(league.flag) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_league)
                }

                matchAdapter.updateMatches(league.fixtures)
                rvMatches.layoutManager = LinearLayoutManager(itemView.context)
                rvMatches.adapter = MatchAdapter(league.fixtures)

                ivExpandCollapse.setOnClickListener {
                    if (rvMatches.visibility == View.VISIBLE) {
                        rvMatches.visibility = View.GONE
                        ivExpandCollapse.setImageResource(R.drawable.ic_expand)
                    } else {
                        rvMatches.visibility = View.VISIBLE
                        ivExpandCollapse.setImageResource(R.drawable.ic_collapse)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemsLeagueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(leagues[position])
    }

    override fun getItemCount() = leagues.size

    fun getLeagues(): List<LeagueWithFixtures> = leagues

    fun updateLeagues(newLeagues: List<LeagueWithFixtures>) {
        leagues = newLeagues
        notifyDataSetChanged()
    }

}
