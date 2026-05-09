package com.example.patientapp.ui.pasien

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.patientapp.databinding.ActivityPasienBinding
import com.example.patientapp.ui.login.LoginActivity
import com.example.patientapp.utils.SessionManager

class PasienActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasienBinding
    private val viewModel: PasienViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: PasienAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        setupHeader()
        setupObserver()
        setupClickListeners()

        // Ambil data pasien
        fetchData()
    }

    private fun setupHeader() {
        val userName = sessionManager.getUserName() ?: "Pengguna"
        val userEmail = sessionManager.getUserEmail() ?: ""
        binding.tvWelcome.text = "Selamat datang,"
        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
    }

    private fun setupRecyclerView() {
        adapter = PasienAdapter()
        binding.rvPasien.apply {
            layoutManager = LinearLayoutManager(this@PasienActivity)
            adapter = this@PasienActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.swipeRefresh.setOnRefreshListener {
            fetchData()
        }

        binding.btnRetry.setOnClickListener {
            fetchData()
        }
    }

    private fun fetchData() {
        val token = sessionManager.getBearerToken()
        viewModel.fetchPasien(token)
    }

    private fun setupObserver() {
        viewModel.pasienState.observe(this) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is PasienState.Loading -> {
                    showLoading()
                }
                is PasienState.Success -> {
                    showData(state.data.size)
                    adapter.submitList(state.data)
                }
                is PasienState.Error -> {
                    showError(state.message)
                }
                is PasienState.Empty -> {
                    showEmpty()
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.rvPasien.visibility = View.GONE
    }

    private fun showData(count: Int) {
        binding.progressBar.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.rvPasien.visibility = View.VISIBLE
        binding.tvJumlahPasien.text = "$count pasien ditemukan"
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.rvPasien.visibility = View.GONE
        binding.tvErrorMessage.text = message
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.rvPasien.visibility = View.GONE
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya, Keluar") { _, _ ->
                logout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun logout() {
        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
