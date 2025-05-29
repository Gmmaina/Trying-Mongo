package com.example.newmongodb.authentication.data

import kotlinx.serialization.Serializable

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String
)
