package com.example.newmongodb.authentication.repository

import com.example.newmongodb.network.AuthService
import com.example.newmongodb.authentication.data.AuthResponse
import com.example.newmongodb.authentication.data.LoginRequest
import com.example.newmongodb.authentication.data.OtpVerificationRequest
import com.example.newmongodb.authentication.data.ResendOtpRequest
import com.example.newmongodb.authentication.data.SignUpRequest
import retrofit2.Response

class AuthRepository(
    private val api: AuthService
) {

    suspend fun signUp(username: String, email: String, password: String): Response<AuthResponse> {
        return api.signUp(SignUpRequest(username, email, password))
    }

    suspend fun login(userIdentifier: String, password: String): Response<AuthResponse> {
        return api.login(LoginRequest(userIdentifier, password))
    }

    suspend fun verifyOtp(email: String, otp: String): Response<AuthResponse> {
        return api.verifyOtp(OtpVerificationRequest(email, otp))
    }

    suspend fun resendOtp(email: String): Response<AuthResponse> {
        return api.resendOtp(ResendOtpRequest(email))
    }
}
