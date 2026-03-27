package com.htv.player.ui.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.htv.player.R
import com.htv.player.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onCategoryClick: (String) -> Unit
) : ListAdapter<String, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedCategory: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelected(category: String?) {
        val oldSelected = selectedCategory
        selectedCategory = category

        currentList.forEachIndexed { index, cat ->
            if (cat == oldSelected || cat == category) {
                notifyItemChanged(index)
            }
        }
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(getItem(position))
                }
            }
        }

        fun bind(category: String) {
            binding.categoryName.text = category

            val isSelected = category == selectedCategory
            binding.root.isSelected = isSelected

            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
            } else {
                binding.root.setBackgroundResource(android.R.color.transparent)
            }

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isSelected) {
                    binding.root.setBackgroundResource(R.drawable.bg_focus_highlight)
                } else if (!isSelected) {
                    binding.root.setBackgroundResource(android.R.color.transparent)
                }
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
