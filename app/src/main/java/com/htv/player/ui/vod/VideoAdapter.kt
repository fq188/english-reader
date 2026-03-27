package com.htv.player.ui.vod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.htv.player.R
import com.htv.player.data.model.Video
import com.htv.player.databinding.ItemVideoCardBinding

class VideoAdapter(
    private val onVideoClick: (Video) -> Unit
) : ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VideoViewHolder(
        private val binding: ItemVideoCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onVideoClick(getItem(position))
                }
            }
        }

        fun bind(video: Video) {
            binding.videoTitle.text = video.title
            binding.videoRating.text = String.format("%.1f", video.rating)
            binding.videoYear.text = video.year?.toString() ?: ""

            Glide.with(binding.root.context)
                .load(video.poster)
                .placeholder(R.drawable.bg_card)
                .error(R.drawable.bg_card)
                .centerCrop()
                .into(binding.videoPoster)

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.root.scaleX = 1.08f
                    binding.root.scaleY = 1.08f
                    binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
                    binding.root.elevation = 8f
                } else {
                    binding.root.scaleX = 1.0f
                    binding.root.scaleY = 1.0f
                    binding.root.setBackgroundResource(R.drawable.bg_card)
                    binding.root.elevation = 4f
                }
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}
