package com.ammar.ammarstoryapp2.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ammar.ammarstoryapp2.R
import com.ammar.ammarstoryapp2.adapter.StoryAdapter
import com.ammar.ammarstoryapp2.data.LoadingStateAdapter
import com.ammar.ammarstoryapp2.databinding.ActivityMainBinding
import com.ammar.ammarstoryapp2.ui.maps.MapsActivity
import com.ammar.ammarstoryapp2.ui.story.UploadStoryActivity
import com.ammar.ammarstoryapp2.ui.welcome.WelcomeActivity
import com.ammar.ammarstoryapp2.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: StoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customActionBar()

        adapter = StoryAdapter()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        setupAction()

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadStoryActivity::class.java)
            startActivity(intent)
        }

        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
            scrollToItem(0)
            Toast.makeText(this@MainActivity, "Story Refresh", Toast.LENGTH_SHORT).show()
        }

    }

    private fun scrollToItem(index: Int) {
        val layoutManager = binding.rvStory.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(index, 0)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }

            R.id.menu_logout -> {
                logoutConfirmation()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("InflateParams")
    private fun customActionBar() {
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.title = ""

        val customView = layoutInflater.inflate(R.layout.custom_actionbarlogo, null)
        supportActionBar?.customView = customView

        val logoImageView = customView.findViewById<ImageView>(R.id.logoImageView)
        logoImageView.setImageResource(R.drawable.img)
    }

    private fun logoutConfirmation() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.confirmation))
            setMessage(getString(R.string.are_you_sure_want_to_logout))
            setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                mainViewModel.logout()
            }
            setNegativeButton(getString(R.string.no), null)
        }.create().show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupAction() {
        binding.apply {
            binding.rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            mainViewModel.listStory.observe(this@MainActivity) {
                adapter.submitData(lifecycle, it)
                showLoading(false)
                binding.swiperefresh.isRefreshing = false
            }
        }
    }
}