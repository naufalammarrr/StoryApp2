package com.ammar.ammarstoryapp2.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ammar.ammarstoryapp2.R
import com.ammar.ammarstoryapp2.databinding.ActivityRegisterBinding
import com.ammar.ammarstoryapp2.ui.login.LoginActivity
import com.ammar.ammarstoryapp2.utils.ViewModelFactory
import com.ammar.ammarstoryapp2.response.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressBar: ProgressBar
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        progressBar = findViewById(R.id.progressBar)

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

    private fun registerUser(name: String, email: String, password: String) {
        registerViewModel.registerUser(name, email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    setupAction()
                    binding.progressBar.visibility = View.GONE

                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.akun_dibuat))
                        setMessage(
                            getString(
                                R.string.akun_dengan_berhasil_dibuat_login_sekarang,
                                email
                            )
                        )
                        setPositiveButton(context.getString(R.string.lanjut)) { _, _ ->
                            val intent =
                                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            startActivity(intent)
                        }
                        show()
                    }
                }

                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle(context.getString(R.string.error))
                        setMessage(result.error)
                        setPositiveButton(context.getString(R.string.ok)) { p0, _ ->
                            p0.dismiss()
                        }
                    }.create().show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.tvLoginDisini.setOnClickListener {
            val intent =
                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            registerUser(name, email, password)
        }

        binding.edRegisterName.addTextChangedListener {
            binding.edRegisterNameLayout.error = null
        }
        binding.edRegisterEmail.addTextChangedListener {
            binding.edRegisterEmailLayout.error = null
        }
        binding.edRegisterPassword.addTextChangedListener {
            binding.edRegisterPasswordLayout.error = null
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(100)
        val description =
            ObjectAnimator.ofFloat(binding.tvRegisterDetail, View.ALPHA, 1f).setDuration(100)
        val etName =
            ObjectAnimator.ofFloat(binding.edRegisterNameLayout, View.ALPHA, 1f).setDuration(100)
        val tvName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(100)
        val etEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmailLayout, View.ALPHA, 1f).setDuration(100)
        val tvEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val etPassword = ObjectAnimator.ofFloat(binding.edRegisterPasswordLayout, View.ALPHA, 1f)
            .setDuration(100)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val button = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)
        val tvLogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(100)
        val tvLoginDisini =
            ObjectAnimator.ofFloat(binding.tvLoginDisini, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                description,
                etName,
                tvName,
                etEmail,
                tvEmail,
                etPassword,
                tvPassword,
                button,
                tvLogin,
                tvLoginDisini
            )
            startDelay = 100
        }.start()
    }
}