package com.nov.storyapp.view.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nov.storyapp.R
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
        enableEdgeToEdge()
        supportActionBar?.apply {
            title = getString(R.string.detail_activity)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(ID)
        if (id != null) {
            val storyIdMessage = getString(R.string.story_id_log, id)
            Log.d("DetailActivity", storyIdMessage)
            viewModel.getDetailStory(id)
        } else {
            val missingStoryIdMessage = getString(R.string.story_id_missing)
            Toast.makeText(this, missingStoryIdMessage, Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, getString(R.string.story_not_found), Toast.LENGTH_SHORT).show()
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