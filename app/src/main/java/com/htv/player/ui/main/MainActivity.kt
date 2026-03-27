package com.htv.player.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.htv.player.R
import com.htv.player.ui.live.LiveTvFragment
import com.htv.player.ui.vod.VodFragment
import com.htv.player.ui.search.SearchActivity
import com.htv.player.ui.download.DownloadFragment
import com.htv.player.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val isTvVersion: Boolean
        get() = packageName.endsWith(".tv")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupFocusListeners()
        setupLayout()

        if (savedInstanceState == null) {
            showVodFragment()
        }
    }

    private fun setupLayout() {
        if (isTvVersion) {
            binding.navigation.visibility = View.VISIBLE
        } else {
            binding.navigation.visibility = View.GONE
        }
    }

    private fun setupNavigation() {
        binding.navLiveTv.setOnClickListener {
            showLiveTvFragment()
        }

        binding.navVod.setOnClickListener {
            showVodFragment()
        }

        binding.navDownloads.setOnClickListener {
            showDownloadFragment()
        }

        binding.navSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun setupFocusListeners() {
        val navItems = listOf(
            binding.navLiveTv,
            binding.navVod,
            binding.navDownloads,
            binding.navSearch
        )

        navItems.forEach { item ->
            item.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    item.setBackgroundResource(R.drawable.bg_focus_highlight)
                } else {
                    item.setBackgroundResource(R.drawable.bg_nav)
                }
            }
        }
    }

    private fun showLiveTvFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LiveTvFragment())
            .commit()
        updateNavSelection(R.id.navLiveTv)
    }

    private fun showVodFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, VodFragment())
            .commit()
        updateNavSelection(R.id.navVod)
    }

    private fun showDownloadFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, DownloadFragment())
            .commit()
        updateNavSelection(R.id.navDownloads)
    }

    private fun updateNavSelection(selectedId: Int) {
        val navItems = mapOf(
            R.id.navLiveTv to binding.navLiveTv,
            R.id.navVod to binding.navVod,
            R.id.navDownloads to binding.navDownloads,
            R.id.navSearch to binding.navSearch
        )

        navItems.forEach { (id, view) ->
            view.isSelected = id == selectedId
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment != null && supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
