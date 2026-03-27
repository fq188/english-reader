package com.htv.player.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.htv.player.R
import com.htv.player.data.model.Video
import com.htv.player.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onResultClick: (Video) -> Unit
) : ListAdapter<Video, SearchResultAdapter.SearchResultViewHolder>(SearchResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onResultClick(getItem(position))
                }
            }
        }

        fun bind(video: Video) {
            binding.videoTitle.text = video.title
            binding.videoRating.text = String.format("%.1f", video.rating)
            binding.videoYear.text = video.year?.toString() ?: ""
            binding.videoCategories.text = video.categories.joinToString(" / ")

            Glide.with(binding.root.context)
                .load(video.poster)
                .placeholder(R.drawable.bg_card)
                .into(binding.videoPoster)

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
                } else {
                    binding.root.setBackgroundResource(R.drawable.bg_card)
                }
            }
        }
    }

    class SearchResultDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}
