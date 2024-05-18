package com.nov.storyapp.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nov.storyapp.R
import com.nov.storyapp.databinding.ActivityHomeBinding
import com.nov.storyapp.databinding.ActivitySettingBinding
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.home.HomeViewModel
import com.nov.storyapp.main.MainActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingViewModel by viewModels { ViewModelFactory.getInstance(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pengecekan status login
        checkLoginStatus()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonLogout.setOnClickListener {
            // Logout dan membersihkan cache
            viewModel.logout()
            clearLoginStatus()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.changeLanguange.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            true
        }

        viewModel.getData().observe(this) { user ->
            binding.tvEmail.text = "Hello, ${user.name}"
        }
    }

    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("StoryAppPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            // Jika tidak login, arahkan pengguna ke LoginActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun clearLoginStatus() {
        // Bersihkan cache status login
        val sharedPreferences = getSharedPreferences("StoryAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("isLoggedIn")
        editor.apply()
    }
}