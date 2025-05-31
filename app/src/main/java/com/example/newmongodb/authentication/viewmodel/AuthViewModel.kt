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
        try {
            // First, try to get response from body (for successful status codes)
            var authResponse = response.body()

            // If body is null, try to parse from errorBody (for error status codes)
            if (authResponse == null) {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val gson = Gson()
                    authResponse = gson.fromJson(errorBody, AuthResponse::class.java)
                }
            }

            // If we successfully parsed an AuthResponse, treat it as success
            // The navigation logic will be handled based on the response content, not status codes
            if (authResponse != null) {
                _authState.value = AuthState.Success(authResponse)
            } else {
                _authState.value = AuthState.Error("Unable to process server response")
            }

        } catch (e: Exception) {
            _authState.value = AuthState.Error("Error processing server response: ${e.message}")
        }
    }
}