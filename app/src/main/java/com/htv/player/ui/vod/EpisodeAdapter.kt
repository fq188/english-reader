package com.htv.player.ui.vod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.htv.player.R
import com.htv.player.data.model.Episode
import com.htv.player.databinding.ItemEpisodeBinding

class EpisodeAdapter(
    private val onEpisodeClick: (Episode) -> Unit
) : ListAdapter<Episode, EpisodeAdapter.EpisodeViewHolder>(EpisodeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EpisodeViewHolder(
        private val binding: ItemEpisodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEpisodeClick(getItem(position))
                }
            }
        }

        fun bind(episode: Episode) {
            binding.episodeTitle.text = "第${episode.episodeNumber}集"

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.root.scaleX = 1.05f
                    binding.root.scaleY = 1.05f
                    binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
                } else {
                    binding.root.scaleX = 1.0f
                    binding.root.scaleY = 1.0f
                    binding.root.setBackgroundResource(R.drawable.bg_card)
                }
            }
        }
    }

    class EpisodeDiffCallback : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem == newItem
        }
    }
}
