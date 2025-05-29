package com.example.newmongodb.authentication.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.newmongodb.databinding.ActivityVerifyEmailBinding
import com.example.newmongodb.network.RetrofitInstance

class VerifyEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyEmailBinding
    private lateinit var email: String
    private var countdownTimer: CountDownTimer? = null
    private val resendDelay: Long = 60000 // 1 minute delay for resend OTP

    private val authViewModel: AuthViewModel by lazy {
        val repository = AuthRepository(RetrofitInstance.authService)
        ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system insets for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = intent.getStringExtra("email").toString()
        binding.emailInstructions.text = "OTP sent to $email"

        if (email.isEmpty()) {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupObservers()
        setupClickListeners()
        startResendTimer() // Start the countdown timer when the activity is created
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { backPress() }
        binding.resendCode.setOnClickListener { resendOtp() }
        binding.verifyOtp.setOnClickListener { verifyOtp() }
    }

    private fun setupObservers() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    showLoading(true)
                }
                is AuthState.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.verifyOtp.isEnabled = !isLoading
    }

    private fun backPress() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun resendOtp() {
        authViewModel.resendOtp(email)
        startResendTimer() // Reset the timer every time the user clicks to resend OTP
    }

    private fun verifyOtp() {
        val otp = binding.tieOtp.text.toString().trim()
        if (otp.isEmpty()) {
            binding.tieOtp.error = "OTP is required"
            return
        }
        authViewModel.verifyOtp(email, otp)
    }

    // Start the countdown timer for the resend button
    private fun startResendTimer() {
        binding.resendCode.isEnabled = false // Disable the resend button
        binding.resendCode.text = "Resend in 60s" // Show countdown text

        countdownTimer = object : CountDownTimer(resendDelay, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the countdown text
                val secondsRemaining = millisUntilFinished / 1000
                binding.resendCode.text = "Resend in ${secondsRemaining}s"
            }

            override fun onFinish() {
                // Enable the resend button once the countdown is over
                binding.resendCode.isEnabled = true
                binding.resendCode.text = "Resend"
            }
        }
        countdownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel() // Cancel the timer if the activity is destroyed
    }
}
