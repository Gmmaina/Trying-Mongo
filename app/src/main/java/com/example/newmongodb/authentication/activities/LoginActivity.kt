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
import com.example.newmongodb.MainActivity
import com.example.newmongodb.authentication.data.AuthResponse
import com.example.newmongodb.authentication.data.AuthState
import com.example.newmongodb.authentication.repository.AuthRepository
import com.example.newmongodb.authentication.viewmodel.AuthViewModel
import com.example.newmongodb.authentication.viewmodel.AuthViewModelFactory
import com.example.newmongodb.databinding.ActivityLoginBinding
import com.example.newmongodb.network.RetrofitInstance

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by lazy {
        val repository = AuthRepository(RetrofitInstance.authService)
        ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system insets for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onClickListeners()
        setObservers()
    }

    private fun setObservers() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    showLoading(true)
                    binding.loginBtn.isEnabled = false
                }
                is AuthState.Success -> {
                    showLoading(false)
                    binding.loginBtn.isEnabled = true
                    handleLoginSuccess(state.response)
                }
                is AuthState.Error -> {
                    showLoading(false)
                    binding.loginBtn.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginBtn.isEnabled = !isLoading
    }

    private fun handleLoginSuccess(response: AuthResponse) {
        Toast.makeText(this, "user is ${response.email}", Toast.LENGTH_SHORT).show()

        // Check verification status from backend response
        when (response.isVerified) {
            // User verified - navigate to main activity
            true -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            false -> {
                // User not verified - navigate to email verification
                if (response.email != null){
                    val intent = Intent(this, VerifyEmailActivity::class.java)
                    intent.putExtra("email", response.email)
                    startActivity(intent)

                } else{
                    Toast.makeText(this, "Login failed: email not provided.", Toast.LENGTH_SHORT).show()
                }
            }
            null -> {
                Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickListeners() {
        binding.loginBtn.setOnClickListener { handleLogin() }
        binding.navToSignUp.setOnClickListener { handleSignUp() }
    }

    private fun handleSignUp() {
        startActivity(Intent(this, VerifyEmailActivity::class.java))
    }

    private fun handleLogin() {
        val userIdentifier = binding.tieUserIdentifier.text.toString().trim()
        val password = binding.tiePassword.text.toString().trim()

        when {
            userIdentifier.isBlank() -> {
                binding.tieUserIdentifier.error = "Username/Email is required"
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
            else -> {
                // Clear any previous errors
                binding.tieUserIdentifier.error = null
                binding.tiePassword.error = null
                authViewModel.login(userIdentifier, password)
            }
        }
    }
}