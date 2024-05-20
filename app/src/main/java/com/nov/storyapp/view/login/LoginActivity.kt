package com.nov.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.android.material.textfield.TextInputLayout
import com.nov.storyapp.R
import com.nov.storyapp.customview.EditTextPassword
import com.nov.storyapp.data.response.LoginResponse
import com.nov.storyapp.databinding.ActivityLoginBinding
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.view.custom.EditTextEmail
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

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val optionCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
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
            val emailEditText = binding.edLoginEmail as EditTextEmail
            val emailValid = emailEditText.validate()
            val passwordEditText = binding.edLoginPassword as EditTextPassword
            val passwordValid = passwordEditText.validate()

            if (!emailValid || !passwordValid) {
                return@setOnClickListener
            }

            viewModel.login(emailEditText.text.toString(), passwordEditText.text.toString())
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }
}
