package com.example.patientapp.ui.pasien

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientapp.api.RetrofitClient
import com.example.patientapp.model.Pasien
import kotlinx.coroutines.launch

sealed class PasienState {
    object Loading : PasienState()
    data class Success(val data: List<Pasien>) : PasienState()
    data class Error(val message: String) : PasienState()
    object Empty : PasienState()
}

class PasienViewModel : ViewModel() {

    private val _pasienState = MutableLiveData<PasienState>()
    val pasienState: LiveData<PasienState> = _pasienState

    fun fetchPasien(bearerToken: String) {
        viewModelScope.launch {
            _pasienState.value = PasienState.Loading
            try {
                val response = RetrofitClient.instance.getPasien(bearerToken)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        if (body.data.isEmpty()) {
                            _pasienState.value = PasienState.Empty
                        } else {
                            _pasienState.value = PasienState.Success(body.data)
                        }
                    } else {
                        _pasienState.value = PasienState.Error(
                            body?.message ?: "Gagal mengambil data pasien"
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Sesi telah habis. Silakan login ulang."
                        403 -> "Akses ditolak"
                        500 -> "Terjadi kesalahan pada server"
                        else -> "Gagal mengambil data (${response.code()})"
                    }
                    _pasienState.value = PasienState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _pasienState.value = PasienState.Error(
                    "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                )
            }
        }
    }
}
