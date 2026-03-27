package com.htv.player.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.htv.player.R
import com.htv.player.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(
    private val onHistoryClick: (String) -> Unit
) : ListAdapter<String, SearchHistoryAdapter.SearchHistoryViewHolder>(SearchHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchHistoryViewHolder(
        private val binding: ItemSearchHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHistoryClick(getItem(position))
                }
            }
        }

        fun bind(keyword: String) {
            binding.historyText.text = keyword

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
                } else {
                    binding.root.setBackgroundResource(android.R.color.transparent)
                }
            }
        }
    }

    class SearchHistoryDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
