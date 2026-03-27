package com.htv.player.ui.vod

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htv.player.R
import com.htv.player.data.model.Video
import com.htv.player.databinding.FragmentVodBinding
import com.htv.player.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VodFragment : Fragment() {

    private var _binding: FragmentVodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VodViewModel by viewModels()

    private lateinit var categoryAdapter: VodCategoryAdapter
    private lateinit var latestAdapter: VideoAdapter
    private lateinit var hotAdapter: VideoAdapter
    private lateinit var highScoreAdapter: VideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryList()
        setupVideoLists()
        setupSwipeRefresh()
        setupObservers()
        setupKeyListener()

        viewModel.loadData()
    }

    private fun setupCategoryList() {
        categoryAdapter = VodCategoryAdapter { category ->
            viewModel.selectCategory(category)
        }

        binding.categoryList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupVideoLists() {
        latestAdapter = VideoAdapter { video -> openVideoDetail(video) }
        hotAdapter = VideoAdapter { video -> openVideoDetail(video) }
        highScoreAdapter = VideoAdapter { video -> openVideoDetail(video) }

        binding.latestList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = latestAdapter
        }

        binding.hotList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hotAdapter
        }

        binding.highScoreList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = highScoreAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryAdapter.submitList(categories)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.latestVideos.collectLatest { videos ->
                latestAdapter.submitList(videos)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hotVideos.collectLatest { videos ->
                hotAdapter.submitList(videos)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.highScoreVideos.collectLatest { videos ->
                highScoreAdapter.submitList(videos)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupKeyListener() {
        binding.swipeRefresh.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        binding.categoryList.requestFocus()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        binding.latestList.requestFocus() ?: binding.hotList.requestFocus() ?: binding.highScoreList.requestFocus()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
    }

    private fun openVideoDetail(video: Video) {
        val intent = Intent(requireContext(), VideoDetailActivity::class.java).apply {
            putExtra(VideoDetailActivity.EXTRA_VIDEO_ID, video.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
