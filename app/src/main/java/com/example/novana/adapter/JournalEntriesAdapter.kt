package com.example.novana.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novana.R

class JournalEntriesAdapter(
    private val entries: MutableList<JournalEntryModel>,
    private val onUpdateClick: (JournalEntryModel) -> Unit,
    private val onDeleteClick: (JournalEntryModel) -> Unit
) : RecyclerView.Adapter<JournalEntriesAdapter.JournalEntryViewHolder>() {

    class JournalEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val entryText: TextView = itemView.findViewById(R.id.entryText)
        val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        val updateButton: Button = itemView.findViewById(R.id.updateButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal_entry, parent, false)
        return JournalEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryText.text = entry.text
        holder.timestampText.text = entry.timestamp
        holder.updateButton.setOnClickListener { onUpdateClick(entry) }
        holder.deleteButton.setOnClickListener { onDeleteClick(entry) }
    }

    override fun getItemCount(): Int = entries.size

    fun removeEntry(entry: JournalEntryModel) {
        val position = entries.indexOf(entry)
        if (position != -1) {
            entries.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateEntry(entry: JournalEntryModel, newText: String) {
        entry.text = newText
        notifyDataSetChanged()
    }
}