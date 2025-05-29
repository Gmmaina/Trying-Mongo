package com.example.newmongodb.network

import com.example.newmongodb.authentication.data.AuthResponse
import com.example.newmongodb.authentication.data.LoginRequest
import com.example.newmongodb.authentication.data.OtpVerificationRequest
import com.example.newmongodb.authentication.data.ResendOtpRequest
import com.example.newmongodb.authentication.data.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<AuthResponse>

    @POST("resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<AuthResponse>


}