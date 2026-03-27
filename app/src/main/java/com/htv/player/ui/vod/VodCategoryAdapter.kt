package com.htv.player.ui.vod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.htv.player.R
import com.htv.player.data.model.Category
import com.htv.player.databinding.ItemVodCategoryBinding

class VodCategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, VodCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemVodCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun setSelectedPosition(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(position)
    }

    inner class CategoryViewHolder(
        private val binding: ItemVodCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    setSelectedPosition(position)
                    onCategoryClick(getItem(position))
                }
            }
        }

        fun bind(category: Category, isSelected: Boolean) {
            binding.categoryName.text = category.name
            binding.root.isSelected = isSelected

            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
            } else {
                binding.root.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
