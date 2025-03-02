package com.example.novana.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.novana.databinding.ActivityLoginBinding
import com.example.novana.viewmodel.UserViewModel
import android.content.Intent
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Observe loading and error states
        viewModel.isLoading.observe(this) { isLoading ->
            // Optionally update UI (e.g., show/hide progress bar)
        }
        viewModel.errorMessage.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
        viewModel.user.observe(this) { user ->
            user?.let {
                Toast.makeText(this, "Login successful! Welcome, ${user.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java)) // Changed to DashboardActivity
                finish() // Finish LoginActivity to prevent back navigation
            }
        }

        // Log the views to ensure they’re not null
        Log.d("LoginActivity", "emailEditText: ${binding.emailEditText}")
        Log.d("LoginActivity", "passwordEditText: ${binding.passwordEditText}")
        Log.d("LoginActivity", "loginBtn: ${binding.loginBtn}")
        Log.d("LoginActivity", "registerText: ${binding.registerText}")

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish() // Optional: Finish LoginActivity to prevent back navigation to it
        }
    }
}