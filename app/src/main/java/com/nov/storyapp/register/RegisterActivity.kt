package com.nov.storyapp.register

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.nov.storyapp.R
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.databinding.ActivityRegisterBinding
import com.nov.storyapp.login.LoginActivity

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
            startActivity(intent)
        }

        setupAction()
        setupTextWatchers()
    }


    private fun setupAction() {
        binding.RegisterButton.setOnClickListener {
            val name = binding.edRegisterName.text?.toString()?:""
            val email = binding.edRegisterEmail.text?.toString()?:""
            val password = binding.edRegisterPassword.text?.toString()?:""

            if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
                setError(binding.nameEditTextLayout, "Harap isi \"Nama\" terlebih dahulu")
                setError(binding.emailEditTextLayout, "Harap isi \"Email\" terlebih dahulu")
                setError(binding.passwordEditTextLayout, "Harap isi \"Password\" terlebih dahulu")
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                setError(binding.nameEditTextLayout, "Harap isi \"Nama\" terlebih dahulu")
                return@setOnClickListener
            } else {
                clearError(binding.nameEditTextLayout)
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

            viewModel.register(name, email, password)

            viewModel.registerResult.observe(this) { result ->
                showLoading(result is ResultState.Loading)

                when (result) {
                    is ResultState.Success -> {
                        showLoading(false)
                        result.data.let { data ->
                            data.message?.let { message ->
                                AlertDialog.Builder(this).apply {
                                    setTitle("Pendaftaran Berhasil")
                                    setMessage("Akun dengan $email sudah dapat digunakan")
                                    setPositiveButton("Lanjut") { _,_ ->
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
                            setTitle("Maaf...")
                            setMessage(result.error)
                            setPositiveButton("Coba Lagi") { _, _ ->
                                create()
                                show()
                            }
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
                    setError(binding.passwordEditTextLayout, "Password harus memiliki setidaknya 8 karakter")
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
