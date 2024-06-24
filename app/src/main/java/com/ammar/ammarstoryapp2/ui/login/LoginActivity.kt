package com.ammar.ammarstoryapp2.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ammar.ammarstoryapp2.R
import com.ammar.ammarstoryapp2.databinding.ActivityLoginBinding
import com.ammar.ammarstoryapp2.response.LoginResult
import com.ammar.ammarstoryapp2.response.Result
import com.ammar.ammarstoryapp2.retrofit.UserModel
import com.ammar.ammarstoryapp2.ui.main.MainActivity
import com.ammar.ammarstoryapp2.ui.register.RegisterActivity
import com.ammar.ammarstoryapp2.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupView()
        setupAction()
        playAnimation()
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
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.tvRegisterDisini.setOnClickListener {
            val intent =
                Intent(this@LoginActivity, RegisterActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        loginViewModel.loginUser(email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setupAction()
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.sukses_login))
                        setMessage(getString(R.string.login_berhasil))
                        setPositiveButton(getString(R.string.lanjut)) { _, _ ->
                            val intent =
                                Intent(this@LoginActivity, MainActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            startActivity(intent)
                        }
                        show()
                    }
                    saveSession(result.data.loginResult)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.error))
                        setMessage(result.error)
                        setPositiveButton(getString(R.string.ok)) { it, _ ->
                            it.dismiss()
                        }
                    }.create().show()
                }
            }
        }
    }

    private fun saveSession(token: LoginResult) {
        loginViewModel.saveSession(UserModel(token.token))
        Log.d("Token disimpan", token.token)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -50f, 50f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvLoginr = ObjectAnimator.ofFloat(binding.tvSignin, View.ALPHA, 1f).setDuration(100)
        val tvLoginDetail =
            ObjectAnimator.ofFloat(binding.tvSigninDetail, View.ALPHA, 1f).setDuration(100)
        val etEmail =
            ObjectAnimator.ofFloat(binding.edEmailLayout, View.ALPHA, 1f).setDuration(100)
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(100)
        val etPassword =
            ObjectAnimator.ofFloat(binding.edLoginPasswordLayout, View.ALPHA, 1f).setDuration(100)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(100)
        val tvRegisterDisini =
            ObjectAnimator.ofFloat(binding.tvRegisterDisini, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                tvLoginr,
                tvLoginDetail,
                etEmail,
                edEmail,
                etPassword,
                edPassword,
                btnLogin,
                tvRegister,
                tvRegisterDisini
            )
            startDelay = 100
        }.start()
    }
}