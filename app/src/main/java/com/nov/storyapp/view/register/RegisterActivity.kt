package com.nov.storyapp.view.register

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
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.databinding.ActivityRegisterBinding
import com.nov.storyapp.view.custom.EditTextEmail
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
    }

    private fun setupAction() {
        binding.RegisterButton.setOnClickListener {
            val nameEditText = binding.edRegisterName.text.toString()
            val emailEditText = binding.edRegisterEmail as EditTextEmail
            val emailValid = emailEditText.validate()
            val passwordEditText = binding.edRegisterPassword as EditTextPassword
            val passwordValid = passwordEditText.validate()

            if (!emailValid || !passwordValid) {
                return@setOnClickListener
            }

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            viewModel.register(nameEditText, email, password)
            viewModel.registerResult.observe(this) { result ->
                showLoading(result is ResultState.Loading)

                when (result) {
                    is ResultState.Success -> {
                        showLoading(false)
                        result.data.let { data ->
                            data.message?.let { message ->
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.register_success_title))
                                    setMessage(getString(R.string.register_success_message))
                                    setPositiveButton(getString(R.string.continuee)) { _, _ ->
                                        finish()
                                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }
}
