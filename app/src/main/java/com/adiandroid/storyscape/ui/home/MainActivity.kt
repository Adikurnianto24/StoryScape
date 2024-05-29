package com.adiandroid.storyscape.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.adiandroid.storyscape.R
import com.adiandroid.storyscape.adapter.LoadingStateAdapter
import com.adiandroid.storyscape.adapter.StoryAdapter
import com.adiandroid.storyscape.data.datastore.UserPreference
import com.adiandroid.storyscape.data.model.response.ListStoryItem
import com.adiandroid.storyscape.databinding.ActivityMainBinding
import com.adiandroid.storyscape.ui.DetailStoryActivity
import com.adiandroid.storyscape.ui.DetailStoryActivity.Companion.DETAIL_STORY
import com.adiandroid.storyscape.ui.auth.LoginActivity
import com.adiandroid.storyscape.ui.home.maps.MapsFragment
import com.adiandroid.storyscape.ui.home.stories.StoriesViewModel
import com.adiandroid.storyscape.ui.post.PostActivity
import com.adiandroid.storyscape.utility.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: ViewModelFactory
    private val storiesViewModel: StoriesViewModel by viewModels { factory }
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        swipeRefreshLayout = binding.swipeRefreshLayout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModelHandler()
        showListStories()
        setupCreateStoryButton()
        setSupportActionBar(findViewById(R.id.toolbar))
        setupLogoutButton()
        setupMapButton()
        setupFragmentManagerBackStackListener()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            refreshStories()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            binding.postProgress.visibility = View.VISIBLE
            delay(1000)
            showListStories()
            refreshStories()
            binding.postProgress.visibility = View.GONE
        }
    }

    private fun refreshStories() {
        storiesViewModel.refreshStories()
        storyAdapter.refresh()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun viewModelHandler() {
        factory = ViewModelFactory.getInstance(binding.root.context)
    }

    private fun openDetailStory(story: ListStoryItem, sharedViews: Array<Pair<View, String>>) {
        val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
        intent.putExtra(DETAIL_STORY, story)

        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            *sharedViews
        )

        startActivity(intent, optionsCompat.toBundle())
    }

    private fun showListStories() {
        storyAdapter = StoryAdapter()
        val storiesRv = binding.recyclerView

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            storiesRv.layoutManager = GridLayoutManager(this, 2)
        } else {
            storiesRv.layoutManager = LinearLayoutManager(this)
        }

        storiesRv.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        storiesViewModel.getListStory.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(
                data: ListStoryItem,
                sharedViews: Array<Pair<View, String>>
            ) {
                openDetailStory(data, sharedViews)
            }
        })
    }

    private fun setupCreateStoryButton() {
        binding.createStoryButton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLogoutButton() {
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val loginPreference = UserPreference(this)
        loginPreference.removeUser()
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_maps -> {
                navigateToMapFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupMapButton() {
        val mapButton = findViewById<ImageButton>(R.id.mapButton)
        mapButton.setOnClickListener {
            navigateToMapFragment()
        }
    }

    private fun navigateToMapFragment() {
        setStoriesVisibility(false)
        binding.createStoryButton.visibility = View.INVISIBLE

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, MapsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setupFragmentManagerBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setStoriesVisibility(true)
                binding.createStoryButton.visibility = View.VISIBLE
            }
        }
    }

    private fun setStoriesVisibility(visible: Boolean) {
        binding.recyclerView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }
}
