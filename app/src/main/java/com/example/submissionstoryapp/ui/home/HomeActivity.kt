package com.example.submissionstoryapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionstoryapp.adapter.LoadingStateAdapter
import com.example.submissionstoryapp.adapter.StoryListAdapter
import com.example.submissionstoryapp.data.local.PreferencesHelper
import com.example.submissionstoryapp.data.local.entity.Story
import com.example.submissionstoryapp.databinding.ActivityHomeBinding
import com.example.submissionstoryapp.ui.addstory.AddStoryActivity
import com.example.submissionstoryapp.ui.login.LoginActivity
import com.example.submissionstoryapp.ui.maps.MapsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        lifecycleScope.launchWhenStarted {
            getAllStories()
        }
        setSwipeRefreshLayout()
        clickButton()
    }

    private fun setSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            getAllStories()
        }
    }

    private fun getAllStories() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getAllStories().observe(this@HomeActivity) {
                    updateRecyclerViewData(it)
                }
            }
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        listAdapter.submitData(lifecycle, stories)

        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun setupRecyclerView() {
        listAdapter = StoryListAdapter()

        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                binding.apply {
                    viewError.root.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    rvStories.isVisible = false
                }
            } else {
                binding.apply {
                    viewError.root.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    rvStories.isVisible = true
                }
            }
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = binding.rvStories
            recyclerView.apply {
                itemAnimator = null
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        listAdapter.retry()
                    },
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun clickButton() {
        binding.apply {
            fab.setOnClickListener {
                val intent = Intent(this@HomeActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
            ivLocation.setOnClickListener {
                val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                startActivity(intent)
            }
            ivKeluar.setOnClickListener {
                preferencesHelper.token = ""
                Intent(this@HomeActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                    finishAffinity()
                }
            }
            ivSetting.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
