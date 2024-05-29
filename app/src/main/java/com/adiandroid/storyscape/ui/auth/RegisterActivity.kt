package com.adiandroid.storyscape.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adiandroid.storyscape.R
import com.adiandroid.storyscape.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        registerViewModel = RegisterViewModel()
        binding.buttonToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.registerButton.setOnClickListener{
            register()
        }
        animationHandler()
        binding.buttonToLogin.setOnClickListener {
            finish()
        }
        setupPasswordVisibilityToggle()
    }
    private fun register() {
        val name = binding.nameEt.text.toString()
        val email = binding.emailEt.text.toString()
        val password = binding.passEt.text.toString()

        when {
            name.isEmpty() -> {
                binding.nameEtLayout.error = getString(R.string.name_notif)
            }
            email.isEmpty() -> {
                binding.emailEtLayout.error = getString(R.string.email_notif)
            }
            password.isEmpty() -> {
                binding.passEtLayout.error = getString(R.string.password_notif)
            }
            else -> {
                registerViewModel.userRegister(name, password, email)

                registerViewModel.isLoading.observe(this) { isLoading ->
                    showLoading(isLoading)
                }

                registerViewModel.isError.observe(this) {
                    errorAlert(it)
                }

                registerViewModel.isSuccess.observe(this) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(this, "Berhasil Membuat Akun", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }

                registerViewModel.registerResult.observe(this) { result ->
                    if (result.error) {
                        Toast.makeText(this, result.description, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun errorAlert(isError: Boolean) {
        if (isError) {
            Toast.makeText(this, "Gagal Daftar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.visibility = View.VISIBLE

        } else {
            binding.loading.visibility = View.GONE
        }
    }
    private fun animationHandler() {
        binding.apply {

            Titletv.alpha = 0f
            nameEtLayout.alpha = 0f
            emailEtLayout.alpha = 0f
            passEtLayout.alpha = 0f
            registerButton.alpha = 0f
            loginTeksTv.alpha = 0f
            buttonToLogin.alpha = 0f
            val titleAnimator = ObjectAnimator.ofFloat(Titletv, View.ALPHA, 0f, 1f)
            titleAnimator.duration = 500

            val nameLayoutAnimator = ObjectAnimator.ofFloat(nameEtLayout, View.ALPHA, 0f, 1f)
            nameLayoutAnimator.duration = 500

            val emailLayoutAnimator = ObjectAnimator.ofFloat(emailEtLayout, View.ALPHA, 0f, 1f)
            emailLayoutAnimator.duration = 500

            val passLayoutAnimator = ObjectAnimator.ofFloat(passEtLayout, View.ALPHA, 0f, 1f)
            passLayoutAnimator.duration = 500
            val loginButtonAnimator = ObjectAnimator.ofFloat(registerButton, View.ALPHA, 0f, 1f)
            loginButtonAnimator.duration = 500

            val loginTeksAnimator = ObjectAnimator.ofFloat(loginTeksTv, View.ALPHA, 0f, 1f)
            loginTeksAnimator.duration = 500

            val buttonToLoginAnimator = ObjectAnimator.ofFloat(buttonToLogin, View.ALPHA, 0f, 1f)
            buttonToLoginAnimator.duration = 500


            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(
                titleAnimator,
                nameLayoutAnimator,
                emailLayoutAnimator,
                passLayoutAnimator,
                loginButtonAnimator,
                loginTeksAnimator,
                buttonToLoginAnimator
            )

            animatorSet.start()
        }
    }
    private fun setupPasswordVisibilityToggle() {
        binding.passEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length >= 8) {
                    binding.passEtLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                } else {
                    binding.passEtLayout.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
        })
    }
}