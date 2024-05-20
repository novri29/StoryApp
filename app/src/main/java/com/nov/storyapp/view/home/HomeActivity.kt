package com.nov.storyapp.view.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nov.storyapp.R
import com.nov.storyapp.view.story.StoryActivity
import com.nov.storyapp.data.response.ListStoryItem
import com.nov.storyapp.databinding.ActivityHomeBinding
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.view.login.LoginActivity
import com.nov.storyapp.view.setting.SettingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.util.Pair

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HomeAdapter
    private val viewModel: HomeViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.app_name)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        setupView()
        itemDecoration()
        adapter = HomeAdapter()

        binding.rvStory.adapter = adapter

        // Load cache
        val cachedStories = loadCache()
        if (cachedStories != null) {
            adapter.setList(cachedStories)
        }

        viewModel.getSession().observe(this) { user ->
            Log.d("token", "onCreate: ${user.token}")
            Log.d("user", "onCreate: ${user.isLogin}, name: ${user.name}")
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                observeStoryItems()
            }
        }

        binding.buttonPost.setOnClickListener {
            val intent = Intent(this@HomeActivity, StoryActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HomeActivity,
                    Pair(binding.buttonPost, "button_post")
            )
            startActivity(intent, optionsCompat.toBundle())
        }
    }

    private fun observeStoryItems() {
        viewModel.storyItem.observe(this) { story ->
            if (story != null) {
                when (story) {
                    is ResultState.Loading -> {
                        showLoading(false)
                    }
                    is ResultState.Success -> {
                        val storyData = story.data.listStory
                        adapter.setList(storyData)
                        showLoading(false)

                        // Save to cache in a coroutine
                        lifecycleScope.launch {
                            saveCache(storyData)
                        }
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "ERROR: ${story.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private suspend fun saveCache(stories: List<ListStoryItem?>?) {
        withContext(Dispatchers.IO) {
            val sharedPreferences = getSharedPreferences("story_cache", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(stories)
            editor.putString("cached_stories", json)
            editor.apply()
        }
    }

    private fun loadCache(): List<ListStoryItem>? {
        val sharedPreferences = getSharedPreferences("story_cache", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("cached_stories", null)
        return if (json != null) {
            val type = object : TypeToken<List<ListStoryItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    private fun itemDecoration() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
    }

    private fun setupView() {
        enableEdgeToEdge() // for Android 10 and above

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) //Android 9 and below
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnSetting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }
}
