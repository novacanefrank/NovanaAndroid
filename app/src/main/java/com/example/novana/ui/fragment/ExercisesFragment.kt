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

class ExercisesFragment : Fragment() {

    private lateinit var exerciseNameEditText: EditText
    private lateinit var addExerciseButton: Button
    private lateinit var backButton: Button
    private lateinit var exercisesRecyclerView: RecyclerView
    private val exercises = mutableListOf<ExerciseModel>()
    private var nextId = 0
    private lateinit var adapter: ExercisesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises, container, false)

        // Initialize views
        exerciseNameEditText = view.findViewById(R.id.exerciseNameEditText)
        addExerciseButton = view.findViewById(R.id.addExerciseButton)
        backButton = view.findViewById(R.id.backButton)
        exercisesRecyclerView = view.findViewById(R.id.exercisesRecyclerView)

        // Set up RecyclerView
        adapter = ExercisesAdapter(exercises, ::onStartStopClick, ::onDeleteClick)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(context)
        exercisesRecyclerView.adapter = adapter

        // Set click listener for Add Exercise button
        addExerciseButton.setOnClickListener {
            val exerciseName = exerciseNameEditText.text.toString().trim()
            if (exerciseName.isNotEmpty()) {
                val newExercise = ExerciseModel(nextId++, exerciseName)
                exercises.add(newExercise)
                adapter.notifyDataSetChanged()
                exerciseNameEditText.text.clear()
                Toast.makeText(context, "Exercise added: $exerciseName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter an exercise name", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Back button
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as DashboardActivity).resetDashboardUI()
        }

        return view
    }

    private fun onStartStopClick(exercise: ExerciseModel) {
        exercise.isRunning = !exercise.isRunning
        adapter.notifyDataSetChanged()
        val action = if (exercise.isRunning) "started" else "stopped"
        Toast.makeText(context, "${exercise.name} $action", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(exercise: ExerciseModel) {
        exercises.remove(exercise)
        adapter.removeExercise(exercise)
        Toast.makeText(context, "${exercise.name} deleted", Toast.LENGTH_SHORT).show()
    }
}