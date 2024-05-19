package com.nov.storyapp.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.util.Pair
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.data.response.LoginResponse
import com.nov.storyapp.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import android.content.Context
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityOptionsCompat
import com.nov.storyapp.R
import com.nov.storyapp.view.home.HomeActivity
import com.nov.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        checkLoginStatus()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupTextWatchers()

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val optionCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@LoginActivity,
                Pair(binding.imageView, "logo"),
                Pair(binding.titleTextView, "title1"),
                Pair(binding.messageTextView, "title2"),
                Pair(binding.emailTextView, "email1"),
                Pair(binding.emailEditTextLayout, "email2"),
                Pair(binding.passwordTextView, "password1"),
                Pair(binding.passwordEditTextLayout, "password2"),
                Pair(binding.loginButton, "login"),
                Pair(binding.tvRegister, "account1"),
                Pair(binding.btnRegister, "account2")

            )
            startActivity(intent, optionCompat.toBundle())
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            // Validasi input
            if (email.isEmpty() && password.isEmpty()) {
                setError(binding.emailEditTextLayout, getString(R.string.error_empty_email))
                setError(binding.passwordEditTextLayout, getString(R.string.error_empty_password))
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                setError(binding.emailEditTextLayout, getString(R.string.error_empty_email))
                return@setOnClickListener
            } else {
                clearError(binding.emailEditTextLayout)
            }

            if (!isValidEmail(email)) {
                setError(binding.emailEditTextLayout, getString(R.string.error_invalid_email))
                return@setOnClickListener
            } else {
                clearError(binding.emailEditTextLayout)
            }

            if (password.isEmpty()) {
                setError(binding.passwordEditTextLayout, getString(R.string.error_empty_password))
                return@setOnClickListener
            } else {
                clearError(binding.passwordEditTextLayout)
            }

            if (password.length < 8) {
                setError(binding.passwordEditTextLayout, getString(R.string.error_short_password))
                return@setOnClickListener
            } else {
                clearError(binding.passwordEditTextLayout)
            }

            viewModel.login(email, password)
            viewModel.loginResult.observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        val response: LoginResponse = result.data
                        saveLoginStatus(true)  // Save login status
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.login_failed_title))
                            setMessage(getString(R.string.login_failed_message))
                            setPositiveButton(getString(R.string.retry)) { _, _ -> }
                            create()
                            show()
                        }
                    }
                }
            }
        }

    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("StoryAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("StoryAppPreferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupTextWatchers() {
        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.length < 8) {
                    setError(binding.passwordEditTextLayout, getString(R.string.error_short_password))
                } else {
                    clearError(binding.passwordEditTextLayout)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val atIndex = email.indexOf('@')
        val dotIndex = email.lastIndexOf('.')

        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length - 1
    }

    private fun setError(textInputLayout: TextInputLayout, error: String) {
        textInputLayout.error = error
        if (textInputLayout == binding.passwordEditTextLayout) {
            textInputLayout.errorIconDrawable = null // Menyembunyikan icon error pada password
        }
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.errorIconDrawable = null // Hapus icon kesalahan
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }
}
