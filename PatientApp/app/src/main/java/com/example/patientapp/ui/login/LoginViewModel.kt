package com.example.patientapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientapp.api.RetrofitClient
import com.example.patientapp.model.LoginRequest
import com.example.patientapp.model.LoginResponse
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email dan password tidak boleh kosong")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = LoginState.Error("Format email tidak valid")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        _loginState.value = LoginState.Success(body)
                    } else {
                        _loginState.value = LoginState.Error(
                            body?.message ?: "Login gagal. Periksa email dan password Anda."
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Email atau password salah"
                        422 -> "Data tidak valid"
                        500 -> "Terjadi kesalahan pada server"
                        else -> "Login gagal (${response.code()})"
                    }
                    _loginState.value = LoginState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                )
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
