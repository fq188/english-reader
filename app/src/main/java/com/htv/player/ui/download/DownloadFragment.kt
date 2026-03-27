package com.htv.player.ui.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.htv.player.R
import com.htv.player.data.model.DownloadStatus
import com.htv.player.data.model.DownloadTask
import com.htv.player.databinding.FragmentDownloadBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DownloadViewModel by viewModels()

    private lateinit var downloadAdapter: DownloadAdapter
    private lateinit var completedAdapter: DownloadAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupRecyclerViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.pauseAllButton.setOnClickListener {
            viewModel.pauseAll()
        }

        binding.resumeAllButton.setOnClickListener {
            viewModel.resumeAll()
        }
    }

    private fun setupRecyclerViews() {
        downloadAdapter = DownloadAdapter(
            onItemClick = { download -> showDownloadOptions(download) },
            onPauseClick = { download -> viewModel.pauseDownload(download.id) },
            onResumeClick = { download -> viewModel.resumeDownload(download.id) },
            onDeleteClick = { download -> viewModel.deleteDownload(download.id) }
        )

        completedAdapter = DownloadAdapter(
            onItemClick = { download -> playDownload(download) },
            onPauseClick = {},
            onResumeClick = {},
            onDeleteClick = { download -> viewModel.deleteDownload(download.id) }
        )

        binding.downloadingList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = downloadAdapter
        }

        binding.completedList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = completedAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeDownloads.collectLatest { downloads ->
                downloadAdapter.submitList(downloads)
                binding.downloadingSection.visibility = if (downloads.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.completedDownloads.collectLatest { downloads ->
                completedAdapter.submitList(downloads)
                binding.completedSection.visibility = if (downloads.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.storageInfo.collectLatest { info ->
                info?.let {
                    binding.storageUsed.text = getString(
                        R.string.storage_used,
                        formatSize(it.usedSize),
                        formatSize(it.totalSize)
                    )
                    binding.storageProgress.progress = it.usedPercentage.toInt()
                    binding.storageProgress.progressDrawable = if (it.isLowSpace) {
                        resources.getDrawable(R.drawable.progress_storage_warning, null)
                    } else {
                        resources.getDrawable(R.drawable.progress_storage, null)
                    }
                    binding.storageWarning.visibility = if (it.isLowSpace) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
            }
        }
    }

    private fun showDownloadOptions(download: DownloadTask) {
        val dialog = DownloadOptionsDialog.newInstance(download)
        dialog.setOnOptionSelectedListener { option ->
            when (option) {
                DownloadOptionsDialog.OPTION_PAUSE -> viewModel.pauseDownload(download.id)
                DownloadOptionsDialog.OPTION_RESUME -> viewModel.resumeDownload(download.id)
                DownloadOptionsDialog.OPTION_DELETE -> viewModel.deleteDownload(download.id)
            }
        }
        dialog.show(childFragmentManager, "download_options")
    }

    private fun playDownload(download: DownloadTask) {
        Toast.makeText(context, "播放: ${download.title}", Toast.LENGTH_SHORT).show()
    }

    private fun formatSize(size: Long): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
