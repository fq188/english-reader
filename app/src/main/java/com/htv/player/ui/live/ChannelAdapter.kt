package com.htv.player.ui.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.htv.player.data.model.Channel
import com.htv.player.databinding.ItemChannelBinding

class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelAdapter.ChannelViewHolder>(ChannelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getChannelAt(position: Int): Channel? {
        return if (position in 0 until itemCount) getItem(position) else null
    }

    inner class ChannelViewHolder(
        private val binding: ItemChannelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onChannelClick(getItem(position))
                }
            }
        }

        fun bind(channel: Channel) {
            binding.channelName.text = channel.name
            binding.channelNumber.text = channel.id.toString()
            binding.currentProgram.text = channel.programName ?: ""

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.root.scaleX = 1.05f
                    binding.root.scaleY = 1.05f
                    binding.root.setBackgroundResource(com.htv.player.R.drawable.bg_focus_highlight)
                } else {
                    binding.root.scaleX = 1.0f
                    binding.root.scaleY = 1.0f
                    binding.root.setBackgroundResource(com.htv.player.R.drawable.bg_card)
                }
            }
        }
    }

    class ChannelDiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }
}
