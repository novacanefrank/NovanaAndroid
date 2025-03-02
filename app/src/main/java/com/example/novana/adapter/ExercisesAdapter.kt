package com.example.novana.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novana.R

class ExercisesAdapter(
    private val exercises: MutableList<ExerciseModel>,
    private val onStartStopClick: (ExerciseModel) -> Unit,
    private val onDeleteClick: (ExerciseModel) -> Unit
) : RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val startStopButton: Button = itemView.findViewById(R.id.startStopButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercises, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        holder.startStopButton.text = if (exercise.isRunning) "Stop" else "Start"
        holder.startStopButton.setOnClickListener { onStartStopClick(exercise) }
        holder.deleteButton.setOnClickListener { onDeleteClick(exercise) }
    }

    override fun getItemCount(): Int = exercises.size

    fun removeExercise(exercise: ExerciseModel) {
        val position = exercises.indexOf(exercise)
        if (position != -1) {
            exercises.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}