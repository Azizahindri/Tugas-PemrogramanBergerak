package com.example.patientapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.patientapp.databinding.ActivityLoginBinding
import com.example.patientapp.ui.pasien.PasienActivity
import com.example.patientapp.utils.SessionManager
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToPasien()
            return
        }

        setupObserver()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }
    }

    private fun setupObserver() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Idle -> {
                    setLoadingState(false)
                }
                is LoginState.Loading -> {
                    setLoadingState(true)
                }
                is LoginState.Success -> {
                    setLoadingState(false)
                    val data = state.response.data!!
                    sessionManager.saveSession(
                        token = data.token,
                        name = data.user.name,
                        email = data.user.email
                    )
                    navigateToPasien()
                }
                is LoginState.Error -> {
                    setLoadingState(false)
                    showError(state.message)
                    viewModel.resetState()
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "Memproses..." else "Masuk"
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(android.R.color.holo_red_dark))
            .setTextColor(getColor(android.R.color.white))
            .show()
    }

    private fun navigateToPasien() {
        startActivity(Intent(this, PasienActivity::class.java))
        finish()
    }
}
