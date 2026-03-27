package com.htv.player.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.htv.player.R
import com.htv.player.data.model.Video
import com.htv.player.databinding.ActivitySearchBinding
import com.htv.player.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchResultAdapter: SearchResultAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupRecyclerViews()
        setupObservers()
        setupKeyListener()

        binding.searchInput.requestFocus()
    }

    private fun setupViews() {
        binding.searchInput.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        searchResultAdapter = SearchResultAdapter { result ->
            playVideo(result)
        }

        binding.searchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchResultAdapter
        }

        historyAdapter = SearchHistoryAdapter { keyword ->
            binding.searchInput.setText(keyword)
            performSearch()
        }

        binding.historyList.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.searchResults.collectLatest { results ->
                searchResultAdapter.submitList(results.videos)
                binding.searchResults.visibility = if (results.videos.isEmpty()) View.GONE else View.VISIBLE
                binding.emptyView.visibility = if (results.videos.isEmpty() && binding.searchInput.text?.isNotEmpty() == true) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.searchHistory.collectLatest { history ->
                historyAdapter.submitList(history)
                binding.historySection.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@SearchActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupKeyListener() {
        binding.root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_BACK -> {
                        finish()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.searchInput.text?.toString() ?: return
        if (query.isBlank()) return

        viewModel.search(query)
        binding.historySection.visibility = View.GONE
        binding.searchResults.visibility = View.VISIBLE
    }

    private fun playVideo(video: Video) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_VIDEO_ID, video.id)
            putExtra(PlayerActivity.EXTRA_EPISODE_ID, video.episodes.firstOrNull()?.id ?: 0)
            putExtra(PlayerActivity.EXTRA_IS_LIVE, false)
        }
        startActivity(intent)
    }
}
