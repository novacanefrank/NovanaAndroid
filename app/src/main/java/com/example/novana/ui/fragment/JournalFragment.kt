package com.example.novana.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novana.R
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.novana.ui.activity.DashboardNavigator
import com.example.novana.viewmodel.JournalViewModel

class JournalFragment : Fragment() {

    private lateinit var journalEntryEditText: EditText
    private lateinit var saveJournalButton: Button
    private lateinit var oldEntriesButton: Button
    private lateinit var backButton: Button
    private lateinit var oldEntriesRecyclerView: RecyclerView
    private lateinit var viewModel: JournalViewModel
    private var isShowingOldEntries = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)

        // Initialize views
        journalEntryEditText = view.findViewById(R.id.journalEntryEditText)
        saveJournalButton = view.findViewById(R.id.saveJournalButton)
        oldEntriesButton = view.findViewById(R.id.oldEntriesButton)
        backButton = view.findViewById(R.id.backButton)
        oldEntriesRecyclerView = view.findViewById(R.id.oldEntriesRecyclerView)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(JournalViewModel::class.java)

        // Set up RecyclerView
        val journalEntries = mutableListOf<JournalEntryModel>()
        val adapter = JournalEntriesAdapter(journalEntries, ::onUpdateClick, ::onDeleteClick)
        oldEntriesRecyclerView.layoutManager = LinearLayoutManager(context)
        oldEntriesRecyclerView.adapter = adapter

        // Observe journal entries from ViewModel
        viewModel.journalEntries.observe(viewLifecycleOwner) { entries ->
            journalEntries.clear()
            journalEntries.addAll(entries)
            adapter.notifyDataSetChanged()
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }

        // Save Button
        saveJournalButton.setOnClickListener {
            val entryText = journalEntryEditText.text.toString().trim()
            if (entryText.isNotEmpty()) {
                val userId = getCurrentUserId()
                val newEntry = JournalEntryModel(id = System.currentTimeMillis().toInt(), text = entryText)
                viewModel.addEntry(newEntry, userId)
                journalEntryEditText.text.clear()
                Toast.makeText(context, "Entry saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please write an entry first", Toast.LENGTH_SHORT).show()
            }
        }

        // Old Entries Button
        oldEntriesButton.setOnClickListener {
            isShowingOldEntries = !isShowingOldEntries
            oldEntriesRecyclerView.visibility = if (isShowingOldEntries) View.VISIBLE else View.GONE
            if (isShowingOldEntries) {
                val userId = getCurrentUserId()
                viewModel.loadEntries(userId)
                Toast.makeText(context, "Showing old entries", Toast.LENGTH_SHORT).show()
            }
        }

        // Back Button
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as? DashboardNavigator)?.resetDashboardUI()
                ?: Log.w("JournalFragment", "DashboardNavigator not implemented")
        }

        return view
    }

    private fun onUpdateClick(entry: JournalEntryModel) {
        val newText = journalEntryEditText.text.toString().trim()
        if (newText.isNotEmpty()) {
            entry.text = newText
            viewModel.updateEntry(entry)
            journalEntryEditText.text.clear()
            oldEntriesRecyclerView.visibility = View.GONE
            isShowingOldEntries = false
            Toast.makeText(context, "Entry updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please enter new text to update", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeleteClick(entry: JournalEntryModel) {
        viewModel.deleteEntry(entry.id)
        Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentUserId(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}
