package com.example.newmongodb.authentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newmongodb.authentication.data.AuthResponse
import com.example.newmongodb.authentication.data.AuthState
import com.example.newmongodb.authentication.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import com.google.gson.Gson
import com.google.gson.JsonObject

class AuthViewModel(private val repository: AuthRepository): ViewModel(){
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    fun signUp(username: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = repository.signUp(username, email, password)
                handleResponse(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun login(userIdentifier: String, password: String){
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = repository.login(userIdentifier, password)
                handleResponse(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resendOtp(email: String){
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = repository.resendOtp(email)
                handleResponse(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    // Fixed: Changed from Long to String to match backend OTP format
    fun verifyOtp(email: String, otp: String){
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(email, otp)
                handleResponse(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    private fun handleResponse(response: Response<AuthResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _authState.value = AuthState.Success(response.body()!!)
        } else if (response.code() == 403 && response.body() != null) {
            // Handle 403 Forbidden - User not verified but response body contains user info
            _authState.value = AuthState.Success(response.body()!!)
        } else {
            // Handle HTTP error codes
            val errorMessage = when (response.code()) {
                400 -> "Bad request"
                401 -> "Invalid credentials"
                409 -> "Conflict - User already exists"
                404 -> "User not found"
                429 -> "Too many requests - please wait before trying again"
                500 -> "Server error"
                else -> {
                    // Try to parse error message from response body
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorJson = gson.fromJson(errorBody, JsonObject::class.java)
                            errorJson.get("message")?.asString ?: "Unknown error"
                        } else {
                            "Unknown error"
                        }
                    } catch (e: Exception) {
                        "Error parsing server response"
                    }
                }
            }
            _authState.value = AuthState.Error(errorMessage)
        }
    }
}