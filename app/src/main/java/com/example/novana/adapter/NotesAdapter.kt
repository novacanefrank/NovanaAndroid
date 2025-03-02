package com.example.novana.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novana.R

class NotesAdapter(
    private val notes: MutableList<NoteModel>,
    private val onUpdateClick: (NoteModel) -> Unit,
    private val onDeleteClick: (NoteModel) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteText: TextView = itemView.findViewById(R.id.noteText)
        val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        val updateButton: Button = itemView.findViewById(R.id.updateButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.noteText.text = note.text
        holder.timestampText.text = note.timestamp
        holder.updateButton.setOnClickListener { onUpdateClick(note) }
        holder.deleteButton.setOnClickListener { onDeleteClick(note) }
    }

    override fun getItemCount(): Int = notes.size

    fun removeNote(note: NoteModel) {
        val position = notes.indexOf(note)
        if (position != -1) {
            notes.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateNote(note: NoteModel, newText: String) {
        note.text = newText
        notifyDataSetChanged()
    }
}