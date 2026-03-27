package com.htv.player.ui.vod

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.htv.player.R
import com.htv.player.data.model.Video
import com.htv.player.databinding.ActivityVideoDetailBinding
import com.htv.player.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoDetailBinding
    private val viewModel: VideoDetailViewModel by viewModels()

    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoId = intent.getIntExtra(EXTRA_VIDEO_ID, -1)
        if (videoId == -1) {
            finish()
            return
        }

        setupViews()
        setupObservers()
        setupKeyListener()

        viewModel.loadVideoDetail(videoId)
    }

    private fun setupViews() {
        episodeAdapter = EpisodeAdapter { episode ->
            playEpisode(episode.id)
        }

        binding.episodeList.apply {
            adapter = episodeAdapter
        }

        binding.playButton.setOnClickListener {
            val video = viewModel.video.value
            if (video != null) {
                val episodeId = video.episodes.firstOrNull()?.id ?: 0
                playEpisode(episodeId)
            }
        }

        binding.downloadButton.setOnClickListener {
            Toast.makeText(this, R.string.download_series, Toast.LENGTH_SHORT).show()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.video.collectLatest { video ->
                video?.let { bindVideo(it) }
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
                    Toast.makeText(this@VideoDetailActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindVideo(video: Video) {
        binding.videoTitle.text = video.title
        binding.videoRating.text = String.format("%.1f", video.rating)
        binding.videoYear.text = video.year?.toString() ?: ""
        binding.videoSynopsis.text = video.synopsis ?: ""
        binding.videoDirector.text = video.director ?: ""

        Glide.with(this)
            .load(video.poster)
            .placeholder(R.drawable.bg_card)
            .into(binding.videoPoster)

        if (video.episodes.isNotEmpty()) {
            episodeAdapter.submitList(video.episodes)
            binding.episodeSection.visibility = View.VISIBLE
        } else {
            binding.episodeSection.visibility = View.GONE
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

    private fun playEpisode(episodeId: Int) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_VIDEO_ID, viewModel.video.value?.id ?: 0)
            putExtra(PlayerActivity.EXTRA_EPISODE_ID, episodeId)
            putExtra(PlayerActivity.EXTRA_IS_LIVE, false)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_VIDEO_ID = "video_id"
    }
}
