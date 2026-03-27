package com.htv.player.ui.live

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
import com.htv.player.R
import com.htv.player.data.model.Channel
import com.htv.player.databinding.FragmentLiveTvBinding
import com.htv.player.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveTvFragment : Fragment() {

    private var _binding: FragmentLiveTvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LiveTvViewModel by viewModels()

    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var currentChannel: Channel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryList()
        setupChannelList()
        setupObservers()
        setupKeyListener()

        viewModel.loadChannels()
    }

    private fun setupCategoryList() {
        categoryAdapter = CategoryAdapter { category ->
            viewModel.selectCategory(category)
        }

        binding.categoryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }
    }

    private fun setupChannelList() {
        channelAdapter = ChannelAdapter { channel ->
            playChannel(channel)
        }

        binding.channelList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = channelAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.channels.collectLatest { channels ->
                channelAdapter.submitList(channels)
                if (channels.isNotEmpty() && currentChannel == null) {
                    currentChannel = channels.first()
                    updateCurrentChannel()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryAdapter.submitList(categories)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCategory.collectLatest { category ->
                categoryAdapter.setSelected(category)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        binding.channelList.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        val currentPos = (binding.channelList.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition()
                        if (currentPos == 0) {
                            binding.categoryList.requestFocus()
                            true
                        } else {
                            false
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        binding.channelList.requestFocus()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        currentChannel?.let { playChannel(it) }
                        true
                    }
                    KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                        val pos = (binding.channelList.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition()
                        channelAdapter.getChannelAt(pos)?.let { playChannel(it) }
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
    }

    private fun playChannel(channel: Channel) {
        currentChannel = channel
        updateCurrentChannel()

        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL_ID, channel.id)
            putExtra(PlayerActivity.EXTRA_IS_LIVE, true)
        }
        startActivity(intent)
    }

    private fun updateCurrentChannel() {
        currentChannel?.let { channel ->
            binding.currentChannelName.text = channel.name
            binding.currentProgram.text = channel.programName ?: getString(R.string.no_epg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
