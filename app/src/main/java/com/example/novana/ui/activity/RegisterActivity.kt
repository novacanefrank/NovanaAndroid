package com.example.novana.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.novana.databinding.ActivityRegisterBinding
import com.example.novana.viewmodel.UserViewModel
import androidx.lifecycle.Observer
import android.content.Intent
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
                Toast.makeText(this, "Registration successful! Welcome, ${user.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java)) // Navigate to MainActivity
                finish()
            }
        }

        // Log the views to ensure they’re not null
        Log.d("RegisterActivity", "usernameEditText: ${binding.usernameEditText}")
        Log.d("RegisterActivity", "emailEditText: ${binding.emailEditText}")
        Log.d("RegisterActivity", "passwordEditText: ${binding.passwordEditText}")
        Log.d("RegisterActivity", "registerBtn: ${binding.registerBtn}")
        Log.d("RegisterActivity", "loginText: ${binding.loginText}")

        binding.registerBtn.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.registerUser(email, password, username)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Optional: Finish RegisterActivity to prevent back navigation
        }
    }
}