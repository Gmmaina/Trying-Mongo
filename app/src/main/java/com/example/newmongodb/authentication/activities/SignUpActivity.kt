package com.example.newmongodb.authentication.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newmongodb.R
import com.example.newmongodb.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
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
    }

    private fun onClickListeners() {
        binding.signUpBtn.setOnClickListener { handleSignUp() }
        binding.navToLogin.setOnClickListener { handleLogin() }
    }

    private fun handleLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}

private fun SignUpActivity.handleSignUp() {
    startActivity(Intent(this, OtpVerificationActivity::class.java))
}
