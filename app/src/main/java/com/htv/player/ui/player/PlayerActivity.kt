package com.htv.player.ui.player

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.htv.player.R
import com.htv.player.databinding.ActivityPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentPosition = 0L
    private var currentMediaItem: MediaItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val channelId = intent.getIntExtra(EXTRA_CHANNEL_ID, -1)
        val videoId = intent.getIntExtra(EXTRA_VIDEO_ID, -1)
        val episodeId = intent.getIntExtra(EXTRA_EPISODE_ID, -1)
        val isLive = intent.getBooleanExtra(EXTRA_IS_LIVE, false)

        viewModel.setPlaybackInfo(videoId, episodeId, channelId, isLive)
        setupObservers()
        setupKeyListener()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> showLoading()
                            Player.STATE_READY -> hideLoading()
                            Player.STATE_ENDED -> onPlaybackEnded()
                            Player.STATE_IDLE -> {}
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Toast.makeText(
                            this@PlayerActivity,
                            getString(R.string.source_unavailable),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.switchToBackupSource()
                    }
                })
            }

        viewModel.currentUrl.observe(this) { url ->
            url?.let {
                val mediaItem = MediaItem.fromUri(it)
                currentMediaItem = mediaItem
                player?.setMediaItem(mediaItem)
                player?.prepare()
            }
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            currentPosition = exoPlayer.currentPosition
            viewModel.savePlaybackProgress(currentPosition)
            exoPlayer.release()
        }
        player = null
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@PlayerActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.channelName.collectLatest { name ->
                binding.channelName.text = name
            }
        }

        lifecycleScope.launch {
            viewModel.currentProgram.collectLatest { program ->
                binding.currentProgram.text = program
            }
        }
    }

    private fun setupKeyListener() {
        binding.playerView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        togglePlayPause()
                        true
                    }
                    KeyEvent.KEYCODE_MENU -> {
                        showControlMenu()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
    }

    private fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun onPlaybackEnded() {
        viewModel.playNextEpisode()
    }

    private fun showControlMenu() {
        binding.controlsOverlay.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_CHANNEL_ID = "channel_id"
        const val EXTRA_VIDEO_ID = "video_id"
        const val EXTRA_EPISODE_ID = "episode_id"
        const val EXTRA_IS_LIVE = "is_live"
    }
}
