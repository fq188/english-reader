package com.htv.player.ui.download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.htv.player.R
import com.htv.player.data.model.DownloadStatus
import com.htv.player.data.model.DownloadTask
import com.htv.player.databinding.ItemDownloadBinding

class DownloadAdapter(
    private val onItemClick: (DownloadTask) -> Unit,
    private val onPauseClick: (DownloadTask) -> Unit,
    private val onResumeClick: (DownloadTask) -> Unit,
    private val onDeleteClick: (DownloadTask) -> Unit
) : ListAdapter<DownloadTask, DownloadAdapter.DownloadViewHolder>(DownloadDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val binding = ItemDownloadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DownloadViewHolder(
        private val binding: ItemDownloadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(download: DownloadTask) {
            binding.downloadTitle.text = download.title
            binding.episodeTitle.text = download.episodeTitle
            binding.quality.text = download.quality

            Glide.with(binding.root.context)
                .load(download.poster)
                .placeholder(R.drawable.bg_card)
                .into(binding.poster)

            when (download.status) {
                DownloadStatus.WAITING -> {
                    binding.status.text = binding.root.context.getString(R.string.download_waiting)
                    binding.status.setTextColor(binding.root.context.getColor(R.color.text_tertiary))
                    binding.pauseResumeButton.setImageResource(android.R.drawable.ic_media_pause)
                }
                DownloadStatus.DOWNLOADING -> {
                    binding.status.text = "${download.progress}%"
                    binding.status.setTextColor(binding.root.context.getColor(R.color.download_progress))
                    binding.progressBar.progress = download.progress
                    binding.speed.text = formatSpeed(download.downloadSpeed)
                    binding.pauseResumeButton.setImageResource(android.R.drawable.ic_media_pause)
                }
                DownloadStatus.PAUSED -> {
                    binding.status.text = binding.root.context.getString(R.string.download_paused)
                    binding.status.setTextColor(binding.root.context.getColor(R.color.download_paused))
                    binding.pauseResumeButton.setImageResource(android.R.drawable.ic_media_play)
                }
                DownloadStatus.COMPLETED -> {
                    binding.status.text = binding.root.context.getString(R.string.downloaded)
                    binding.status.setTextColor(binding.root.context.getColor(R.color.success))
                    binding.progressBar.progress = 100
                    binding.speed.text = ""
                }
                DownloadStatus.FAILED -> {
                    binding.status.text = binding.root.context.getString(R.string.download_failed)
                    binding.status.setTextColor(binding.root.context.getColor(R.color.download_error))
                    binding.pauseResumeButton.setImageResource(android.R.drawable.ic_media_play)
                }
            }

            binding.pauseResumeButton.setOnClickListener {
                when (download.status) {
                    DownloadStatus.DOWNLOADING, DownloadStatus.WAITING -> onPauseClick(download)
                    DownloadStatus.PAUSED, DownloadStatus.FAILED -> onResumeClick(download)
                    else -> {}
                }
            }

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.root.scaleX = 1.02f
                    binding.root.scaleY = 1.02f
                    binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
                } else {
                    binding.root.scaleX = 1.0f
                    binding.root.scaleY = 1.0f
                    binding.root.setBackgroundResource(R.drawable.bg_card)
                }
            }
        }

        private fun formatSpeed(speed: Long): String {
            val kb = speed / 1024.0
            val mb = kb / 1024.0

            return when {
                mb >= 1 -> String.format("%.2f MB/s", mb)
                kb >= 1 -> String.format("%.2f KB/s", kb)
                else -> "$speed B/s"
            }
        }
    }

    class DownloadDiffCallback : DiffUtil.ItemCallback<DownloadTask>() {
        override fun areItemsTheSame(oldItem: DownloadTask, newItem: DownloadTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DownloadTask, newItem: DownloadTask): Boolean {
            return oldItem == newItem
        }
    }
}
