package com.example.novana.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novana.R
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.novana.ui.activity.DashboardActivity

class JournalFragment : Fragment() {

    private lateinit var journalEntryEditText: EditText
    private lateinit var saveJournalButton: Button
    private lateinit var oldEntriesButton: Button
    private lateinit var backButton: Button
    private lateinit var oldEntriesRecyclerView: RecyclerView
    private val journalEntries = mutableListOf<JournalEntryModel>()
    private var nextId = 0
    private lateinit var adapter: JournalEntriesAdapter
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

        // Set up RecyclerView
        adapter = JournalEntriesAdapter(journalEntries, ::onUpdateClick, ::onDeleteClick)
        oldEntriesRecyclerView.layoutManager = LinearLayoutManager(context)
        oldEntriesRecyclerView.adapter = adapter

        // Set click listener for Save button
        saveJournalButton.setOnClickListener {
            val entryText = journalEntryEditText.text.toString().trim()
            if (entryText.isNotEmpty()) {
                val newEntry = JournalEntryModel(nextId++, entryText)
                journalEntries.add(newEntry)
                journalEntryEditText.text.clear()
                Toast.makeText(context, "Entry saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please write an entry first", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Old Entries button
        oldEntriesButton.setOnClickListener {
            isShowingOldEntries = !isShowingOldEntries
            oldEntriesRecyclerView.visibility = if (isShowingOldEntries) View.VISIBLE else View.GONE
            if (isShowingOldEntries) {
                adapter.notifyDataSetChanged()
                Toast.makeText(context, "Showing old entries", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Back button
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as DashboardActivity).resetDashboardUI()
        }

        return view
    }

    private fun onUpdateClick(entry: JournalEntryModel) {
        val newText = journalEntryEditText.text.toString().trim()
        if (newText.isNotEmpty()) {
            adapter.updateEntry(entry, newText)
            journalEntryEditText.text.clear()
            oldEntriesRecyclerView.visibility = View.GONE
            isShowingOldEntries = false
            Toast.makeText(context, "Entry updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please enter new text to update", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeleteClick(entry: JournalEntryModel) {
        journalEntries.remove(entry)
        adapter.removeEntry(entry)
        Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
    }
}