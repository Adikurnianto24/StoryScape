package com.adiandroid.storyscape.ui.auth

import LoginViewModelFactory
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.adiandroid.storyscape.R
import com.adiandroid.storyscape.data.api.ApiService
import com.adiandroid.storyscape.data.datastore.UserPreference
import com.adiandroid.storyscape.data.model.response.AuthResponse
import com.adiandroid.storyscape.data.model.user.UserModel
import com.adiandroid.storyscape.databinding.ActivityLoginBinding
import com.adiandroid.storyscape.ui.home.MainActivity
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreference = UserPreference(this)
        if (userPreference.isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiService = ApiService.create()
        val factory = LoginViewModelFactory(apiService)
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        buttonActionSetup()
        animationHandler()
        observeLoginResult()
        setupPasswordVisibilityToggle()
    }

    private fun buttonActionSetup() {
        binding.apply {
            loginButton.setOnClickListener {
                login()
            }
            buttonToRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun login() {
        val email = binding.emailEt.text.toString()
        val password = binding.passEt.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) binding.emailEtLayout.error = getString(R.string.email_notif)
            if (password.isEmpty()) binding.passEtLayout.error = getString(R.string.password_notif)
        } else {
            binding.loginProgress.visibility = View.VISIBLE
            loginViewModel.userLogin(email, password)
        }
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this) { loginUserData ->
            binding.loginProgress.visibility = View.GONE
            if (!loginUserData.error) {
                loginHandler(loginUserData)
                Toast.makeText(this, "Berhasil Login", Toast.LENGTH_SHORT).show()
            } else {
                errorAlert(true)
            }
        }

        loginViewModel.isError.observe(this) { isError ->
            if (isError) {
                binding.loginProgress.visibility = View.GONE
                errorAlert(isError)
            }
        }
    }

    private fun errorAlert(isError: Boolean) {
        if (isError) {
            Toast.makeText(this, "GAGAL MASUK", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginHandler(loginUserData: AuthResponse) {
        saveUserData(loginUserData)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserData(loginUserData: AuthResponse) {
        val loginPreference = UserPreference(this)
        val loginResult = loginUserData.loginResult
        val userModel = UserModel(
            name = loginResult.name, userId = loginResult.userId, token = loginResult.token
        )
        loginPreference.setLogin(userModel)
    }

    private fun animationHandler() {
        binding.apply {
            Titletv.alpha = 0f
            emailEtLayout.alpha = 0f
            passEtLayout.alpha = 0f
            loginButton.alpha = 0f
            regsiterTeksTv.alpha = 0f
            buttonToRegister.alpha = 0f
            val titleAnimator = ObjectAnimator.ofFloat(Titletv, View.ALPHA, 0f, 1f).apply { duration = 1000 }
            val emailLayoutAnimator = ObjectAnimator.ofFloat(emailEtLayout, View.ALPHA, 0f, 1f).apply { duration = 1000 }
            val passLayoutAnimator = ObjectAnimator.ofFloat(passEtLayout, View.ALPHA, 0f, 1f).apply { duration = 1000 }
            val loginButtonAnimator = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 0f, 1f).apply { duration = 1000 }
            val registerTextAnimator = ObjectAnimator.ofFloat(regsiterTeksTv, View.ALPHA, 0f, 1f).apply { duration = 1000 }
            val registerButtonAnimator = ObjectAnimator.ofFloat(buttonToRegister, View.ALPHA, 0f, 1f).apply { duration = 1000 }

            val animatorSet = AnimatorSet().apply {
                playSequentially(
                    titleAnimator,
                    emailLayoutAnimator,
                    passLayoutAnimator,
                    loginButtonAnimator,
                    registerTextAnimator,
                    registerButtonAnimator
                )
            }
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