package com.example.newmongodb.authentication.data

import kotlinx.serialization.Serializable

data class LoginRequest(
    val userIdentifier: String,
    val password: String
)
