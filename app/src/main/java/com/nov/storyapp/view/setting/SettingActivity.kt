package com.nov.storyapp.view.setting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nov.storyapp.R
import com.nov.storyapp.databinding.ActivitySettingBinding
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.view.main.MainActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.apply {
            title = getString(R.string.setting)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pengecekan status login
        checkLoginStatus()

        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.changeLanguange.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        viewModel.getData().observe(this) { user ->
            binding.tvEmail.text = getString(R.string.hello, user.name)
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

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                // Logout and clear login status
                viewModel.logout()
                clearLoginStatus()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                // Dismiss the dialog
                dialog.dismiss()
            }
            .create()
            .show()
    }

}