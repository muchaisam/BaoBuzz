package com.example.baobuzz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baobuzz.R
import com.example.baobuzz.daos.Transfer
import com.example.baobuzz.databinding.ItemTransferBinding
import com.example.baobuzz.models.ApiTransfer

class TransfersAdapter : ListAdapter<Transfer, TransferViewHolder>(TransferDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        val binding = ItemTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TransferViewHolder(private val binding: ItemTransferBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(transfer: Transfer) {
        binding.apply {
            playerName.text = transfer.playerName
            fromTeam.text = transfer.fromTeam
            toTeam.text = transfer.toTeam

            transferDate.text = transfer.transferDate
            transferFee.text = transfer.transferType
        }
    }
}

class TransferDiffCallback : DiffUtil.ItemCallback<Transfer>() {
    override fun areItemsTheSame(oldItem: Transfer, newItem: Transfer) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Transfer, newItem: Transfer) = oldItem == newItem
}