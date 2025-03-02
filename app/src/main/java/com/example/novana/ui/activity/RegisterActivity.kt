package com.example.novana.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.novana.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
                register(username, email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()  // Optional: Finish RegisterActivity to prevent back navigation
        }
    }

    private fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Optionally save username to Firebase Realtime Database or Firestore
                    Toast.makeText(this, "Registration successful! Welcome, ${user?.email}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))  // Or LoginActivity, depending on your flow
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}