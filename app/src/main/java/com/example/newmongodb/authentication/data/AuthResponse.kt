package com.example.newmongodb.authentication.data


data class AuthResponse(
    val message: String,
    val isVerified: Boolean? = null,
    val email: String? = null
)
