package com.example.newmongodb.authentication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.newmongodb.R
import com.example.newmongodb.authentication.data.AuthResponse
import com.example.newmongodb.authentication.data.AuthState
import com.example.newmongodb.authentication.repository.AuthRepository
import com.example.newmongodb.authentication.viewmodel.AuthViewModel
import com.example.newmongodb.authentication.viewmodel.AuthViewModelFactory
import com.example.newmongodb.databinding.ActivitySignUpBinding
import com.example.newmongodb.network.RetrofitInstance

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val authViewModel: AuthViewModel by lazy {
        val repository = AuthRepository(RetrofitInstance.authService)
        ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onClickListeners()
        setObservers()
    }

    private fun onClickListeners() {
        binding.signUpBtn.setOnClickListener { handleSignUp() }
        binding.navToLoginTv.setOnClickListener { handleLogin() }
    }

    private fun handleLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun setObservers() {
        authViewModel.authState.observe(this){ state ->
            when(state){
                is AuthState.Loading -> {
                    showLoading(true)
                }
                is AuthState.Success -> {
                    showLoading(false)
                    handleSignUpSuccess(state.response)
                }
                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun SignUpActivity.handleSignUp() {
        val username = binding.tieUsername.text.toString().trim()
        val email = binding.tieEmail.text.toString().trim()
        val password = binding.tiePassword.text.toString().trim()
        val confirmPassword = binding.tieConfirmPassword.text.toString().trim()
        when {
            username.isBlank() -> {
                binding.tieUsername.error = "Username is required"
                return
            }
            email.isBlank() -> {
                binding.tieEmail.error = "Email is required"
                return
            }
            password.isBlank() -> {
                binding.tiePassword.error = "Password is required"
                return
            }
            password.length < 6 -> {
                binding.tiePassword.error = "Password must be at least 6 characters"
                return
            }
            confirmPassword.isBlank() -> {
                binding.tieConfirmPassword.error = "Confirm Password is required"
                return
            }
            password != confirmPassword -> {
                binding.tieConfirmPassword.error = "Passwords do not match"
                return
            }
            else -> {
                // Clear any previous errors
                binding.tieUsername.error = null
                binding.tieEmail.error = null
                binding.tiePassword.error = null
                binding.tieConfirmPassword.error = null
                authViewModel.signUp(username, email, password)
            }
        }
    }

    private fun SignUpActivity.showLoading(isLoading: Boolean) {
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signUpBtn.isEnabled = !isLoading
    }

    private fun SignUpActivity.handleSignUpSuccess(response: AuthResponse) {
        if (response.email != null){
            val intent = Intent(this, VerifyEmailActivity::class.java)
            intent.putExtra("email", response.email)
            startActivity(intent)
        } else{
            Toast.makeText(this, "Sign Up failed: email not provided.", Toast.LENGTH_SHORT).show()
        }
    }
}



