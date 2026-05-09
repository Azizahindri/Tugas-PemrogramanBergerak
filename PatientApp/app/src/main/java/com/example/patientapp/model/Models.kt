package com.example.patientapp.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LoginData?
)

data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)

data class PasienResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Pasien>
)

data class Pasien(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("no_telepon") val noTelepon: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
