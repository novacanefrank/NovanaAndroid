package com.example.novana.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novana.R
import com.example.novana.databinding.ActivityDashboardBinding
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

// Import your actual Fragment classes
import com.example.novana.ui.fragment.JournalFragment
import com.example.novana.ui.fragment.ExercisesFragment
import com.example.novana.ui.fragment.GoalsFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var notesRecyclerView: RecyclerView
    private val notes = mutableListOf<NoteModel>()
    private var nextId = 0
    private lateinit var adapter: NotesAdapter
    private var isShowingOldNotes = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        adapter = NotesAdapter(notes, ::onUpdateClick, ::onDeleteClick)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = adapter

        // Log the views to ensure they’re not null
        Log.d("DashboardActivity", "notesEditText: ${binding.notesEditText}")
        Log.d("DashboardActivity", "userProfileIcon: ${binding.userProfileIcon}")
        Log.d("DashboardActivity", "journalCard: ${binding.journalCard}")
        Log.d("DashboardActivity", "dailyExercisesCard: ${binding.dailyExercisesCard}")
        Log.d("DashboardActivity", "setGoalsCard: ${binding.setGoalsCard}")
        Log.d("DashboardActivity", "saveNotesButton: ${binding.saveNotesButton}")
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
            binding.notesSection.visibility = View.GONE // Hide notes section
            Toast.makeText(this, "Journal clicked", Toast.LENGTH_SHORT).show()
        }

        binding.dailyExercisesCard.setOnClickListener {
            replaceFragment(ExercisesFragment())
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.gridContainer.visibility = View.GONE
            binding.notesSection.visibility = View.GONE // Hide notes section
            Toast.makeText(this, "Daily Exercises clicked", Toast.LENGTH_SHORT).show()
        }

        binding.setGoalsCard.setOnClickListener {
            replaceFragment(GoalsFragment())
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.gridContainer.visibility = View.GONE
            binding.notesSection.visibility = View.GONE // Hide notes section
            Toast.makeText(this, "Set Goals clicked", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for Show Old Notes button
        binding.showOldNotesButton.setOnClickListener {
            isShowingOldNotes = !isShowingOldNotes
            notesRecyclerView.visibility = if (isShowingOldNotes) View.VISIBLE else View.GONE
            if (isShowingOldNotes) {
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Showing old notes", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Save button
        binding.saveNotesButton.setOnClickListener {
            val noteText = binding.notesEditText.text.toString().trim()
            if (noteText.isNotEmpty()) {
                val newNote = NoteModel(nextId++, noteText)
                notes.add(newNote)
                binding.notesEditText.text.clear()
                Toast.makeText(this, "Note saved: $noteText", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please write some notes first", Toast.LENGTH_SHORT).show()
            }
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
            binding.notesSection.visibility = View.VISIBLE // Show notes section when back
            Log.d("DashboardActivity", "Popped back stack, fragments remaining: ${supportFragmentManager.backStackEntryCount}")
        } else {
            super.onBackPressed()
        }
    }

    // Public method to reset the UI
    fun resetDashboardUI() {
        binding.fragmentContainer.visibility = View.GONE
        binding.gridContainer.visibility = View.VISIBLE
        binding.notesSection.visibility = View.VISIBLE // Ensure notes section is visible
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
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun onUpdateClick(note: NoteModel) {
        val newText = binding.notesEditText.text.toString().trim()
        if (newText.isNotEmpty()) {
            adapter.updateNote(note, newText)
            binding.notesEditText.text.clear()
            notesRecyclerView.visibility = View.GONE
            isShowingOldNotes = false
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter new text to update", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeleteClick(note: NoteModel) {
        notes.remove(note)
        adapter.removeNote(note)
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
    }
}