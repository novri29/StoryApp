package com.nov.storyapp.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nov.storyapp.MainActivity
import com.nov.storyapp.ResultState
import com.nov.storyapp.ViewModelFactory
import com.nov.storyapp.data.response.LoginResponse
import com.nov.storyapp.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import android.content.Context
import android.content.SharedPreferences

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLoginStatus()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTextWatchers()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            // Validasi input
            if (email.isEmpty() && password.isEmpty()) {
                setError(binding.emailEditTextLayout, "Harap isi \"Email\" terlebih dahulu")
                setError(binding.passwordEditTextLayout, "Harap isi \"Password\" terlebih dahulu")
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                setError(binding.emailEditTextLayout, "Harap isi \"Email\" terlebih dahulu")
                return@setOnClickListener
            } else {
                clearError(binding.emailEditTextLayout)
            }

            if (!isValidEmail(email)) {
                setError(binding.emailEditTextLayout, "\"Email\" harus valid menggunakan @")
                return@setOnClickListener
            } else {
                clearError(binding.emailEditTextLayout)
            }

            if (password.isEmpty()) {
                setError(binding.passwordEditTextLayout, "Harap isi \"Password\" terlebih dahulu")
                return@setOnClickListener
            } else {
                clearError(binding.passwordEditTextLayout)
            }

            if (password.length < 8) {
                setError(binding.passwordEditTextLayout, "Password harus memiliki setidaknya 8 karakter")
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
                        AlertDialog.Builder(this).apply {
                            setTitle("Login Berhasil!")
                            setMessage("Anda login sebagai ${response.loginResult?.name}")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Login Gagal")
                            setMessage("Email atau Password yang anda masukkan salah")
                            setPositiveButton("Coba Lagi") { _, _ -> }
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
            val intent = Intent(this, MainActivity::class.java)
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
                    setError(binding.passwordEditTextLayout, "Password harus memiliki setidaknya 8 karakter")
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
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }
}
