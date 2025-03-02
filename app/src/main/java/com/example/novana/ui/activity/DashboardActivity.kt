package com.example.novana.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.novana.R
import com.example.novana.databinding.ActivityDashboardBinding
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog

// Import your actual Fragment classes
import com.example.novana.ui.fragment.JournalFragment
import com.example.novana.ui.fragment.ExercisesFragment
import com.example.novana.ui.fragment.GoalsFragment

class DashboardActivity : AppCompatActivity(), DashboardNavigator {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Log the views to ensure they’re not null
        Log.d("DashboardActivity", "userProfileIcon: ${binding.userProfileIcon}")
        Log.d("DashboardActivity", "journalCard: ${binding.journalCard}")
        Log.d("DashboardActivity", "dailyExercisesCard: ${binding.dailyExercisesCard}")
        Log.d("DashboardActivity", "setGoalsCard: ${binding.setGoalsCard}")
        Log.d("DashboardActivity", "fragmentContainer: ${binding.fragmentContainer}")

        // Set click listener for User Profile Icon
        binding.userProfileIcon.setOnClickListener {
            showProfileDialog()
        }

        // Set click listeners for the three cards with Fragment navigation
        binding.journalCard.setOnClickListener {
            replaceFragment(JournalFragment())
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.gridContainer.visibility = View.GONE
            Toast.makeText(this, "Journal clicked", Toast.LENGTH_SHORT).show()
        }

        binding.dailyExercisesCard.setOnClickListener {
            replaceFragment(ExercisesFragment())
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.gridContainer.visibility = View.GONE
            Toast.makeText(this, "Daily Exercises clicked", Toast.LENGTH_SHORT).show()
        }

        binding.setGoalsCard.setOnClickListener {
            replaceFragment(GoalsFragment())
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.gridContainer.visibility = View.GONE
            Toast.makeText(this, "Set Goals clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
        Log.d("DashboardActivity", "Replacing fragment with ${fragment.javaClass.simpleName}")
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            binding.fragmentContainer.visibility = View.GONE
            binding.gridContainer.visibility = View.VISIBLE
            Log.d("DashboardActivity", "Popped back stack, fragments remaining: ${supportFragmentManager.backStackEntryCount}")
        } else {
            super.onBackPressed()
        }
    }

    override fun resetDashboardUI() {
        binding.fragmentContainer.visibility = View.GONE
        binding.gridContainer.visibility = View.VISIBLE
    }

    private fun showProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_profile, null)
        val logoutButton = dialogView.findViewById<Button>(R.id.logoutButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Profile")
            .setCancelable(true)
            .create()

        logoutButton.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            performLogout()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performLogout() {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        auth.signOut()
        val intent = android.content.Intent(this, LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}