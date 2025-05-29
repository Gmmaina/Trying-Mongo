package com.example.newmongodb.authentication.data

data class OtpVerificationRequest(
    val email: String,
    val otp: String
)
