package com.nov.storyapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nov.storyapp.data.api.ApiConfig
import com.nov.storyapp.databinding.ActivityDetailStoryBinding
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.helper.toDateFormat

class DetailStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(ID)
        if (id != null) {
            Log.d("DetailActivity", "Story ID: $id")
            viewModel.getDetailStory(id)
        } else {
            Toast.makeText(this, "Story ID is missing", Toast.LENGTH_SHORT).show()
        }

        viewModel.story.observe(this) { detailStoryState ->
            when (detailStoryState) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    val detailStory = detailStoryState.data.story
                    if (detailStory != null) {
                        binding.apply {
                            tvDetailName.text = detailStory.name
                            tvDetailDate.text = detailStory.createdAt?.toDateFormat()
                            tvDetailDesc.text = detailStory.description
                            Glide.with(root.context)
                                .load(detailStory.photoUrl)
                                .into(ivDetailPhoto)
                        }
                    } else {
                        Toast.makeText(this, "Story not found", Toast.LENGTH_SHORT).show()
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error: ${detailStoryState.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }

    companion object {
        const val ID = "ID"
    }
}