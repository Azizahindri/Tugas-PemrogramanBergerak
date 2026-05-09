package com.example.patientapp.api

import com.example.patientapp.model.LoginRequest
import com.example.patientapp.model.LoginResponse
import com.example.patientapp.model.PasienResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("pasien")
    suspend fun getPasien(
        @Header("Authorization") token: String
    ): Response<PasienResponse>
}
