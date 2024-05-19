package com.nov.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.nov.storyapp.view.home.HomeActivity
import com.nov.storyapp.databinding.ActivityMainBinding
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.view.login.LoginActivity
import com.nov.storyapp.view.register.RegisterActivity
import com.nov.storyapp.view.story.StoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel : MainViewModel by viewModels { viewModelFactory }

        viewModel.getLoginData().observe(this) {

            if (it.isLogin) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(binding.ivImage, "logo")
                    )

                startActivity(intent, optionsCompat.toBundle())
                finish()
            }
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            val optionCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(binding.ivImage, "logo"),
                    Pair(binding.titleTextView, "title1"),
                    Pair(binding.descTextView, "title2"),
                    Pair(binding.loginButton, "login"),
                    Pair(binding.registerButton, "register")
                )
            startActivity(intent, optionCompat.toBundle())
        }
        binding.registerButton.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            val optionCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(binding.ivImage, "logo"),
                    Pair(binding.titleTextView, "title1"),
                    Pair(binding.descTextView, "title2"),
                    Pair(binding.loginButton, "login"),
                    Pair(binding.registerButton, "register")
                )
            startActivity(intent, optionCompat.toBundle())
        }
        binding.guestButton.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(binding.ivImage, "logo"),
                    Pair(binding.titleTextView, "title1"),
                    Pair(binding.descTextView, "title2"),
                    Pair(binding.guestButton, "guest")
            )
            startActivity(intent, optionsCompat.toBundle())
        }
    }
}