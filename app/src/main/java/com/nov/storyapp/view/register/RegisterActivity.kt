package com.nov.storyapp.view.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.nov.storyapp.R
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.databinding.ActivityRegisterBinding
import com.nov.storyapp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            val optionCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this@RegisterActivity,
                Pair(binding.ivImage, "logo"),
                Pair(binding.titleTextView, "title1"),
                Pair(binding.nameTextView, "name1"),
                Pair(binding.nameEditTextLayout, "name2"),
                Pair(binding.emailTextView, "email1"),
                Pair(binding.emailEditTextLayout, "email2"),
                Pair(binding.passwordTextView, "password1"),
                Pair(binding.passwordEditTextLayout, "password2"),
                Pair(binding.RegisterButton, "register"),
                Pair(binding.tvLogin, "register1"),
                Pair(binding.btnLogin, "register2")
            )
            startActivity(intent, optionCompat.toBundle())
        }

        setupAction()
        setupTextWatchers()
    }


    private fun setupAction() {
        binding.RegisterButton.setOnClickListener {
            val name = binding.edRegisterName.text?.toString() ?: ""
            val email = binding.edRegisterEmail.text?.toString() ?: ""
            val password = binding.edRegisterPassword.text?.toString() ?: ""

            if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
                setError(binding.nameEditTextLayout, getString(R.string.error_empty_name))
                setError(binding.emailEditTextLayout, getString(R.string.error_empty_email))
                setError(binding.passwordEditTextLayout, getString(R.string.error_empty_password))
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                setError(binding.nameEditTextLayout, getString(R.string.error_empty_name))
                return@setOnClickListener
            } else {
                clearError(binding.nameEditTextLayout)
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

            viewModel.register(name, email, password)

            viewModel.registerResult.observe(this) { result ->
                showLoading(result is ResultState.Loading)

                when (result) {
                    is ResultState.Success -> {
                        showLoading(false)
                        result.data.let { data ->
                            data.message?.let { message ->
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.register_success_title))
                                    setMessage(getString(R.string.register_success_message, email))
                                    setPositiveButton(getString(R.string.continuee)) { _, _ ->
                                        finish()
                                        startActivity(
                                            Intent(
                                                this@RegisterActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.register_failed_title))
                            setMessage(result.error)
                            setPositiveButton(getString(R.string.retry)) { _, _ -> }
                            create()
                            show()
                        }
                    }

                    is ResultState.Loading -> showLoading(true)
                }
            }
        }
    }

        private fun isValidEmail(email: String): Boolean {
        val atIndex = email.indexOf('@')
        val dotIndex = email.lastIndexOf('.')

        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length - 1
    }

    private fun setupTextWatchers() {
        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
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

    private fun setError(textInputLayout: TextInputLayout, error: String) {
        textInputLayout.error = error
        if (textInputLayout == binding.passwordEditTextLayout) {
            textInputLayout.errorIconDrawable = null
        }
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.errorIconDrawable = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }
}
