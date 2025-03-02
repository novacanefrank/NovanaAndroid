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
import com.example.novana.viewmodel.ExercisesViewModel

class ExercisesFragment : Fragment() {

    private lateinit var exerciseNameEditText: EditText
    private lateinit var addExerciseButton: Button
    private lateinit var backButton: Button
    private lateinit var exercisesRecyclerView: RecyclerView
    private val exercises = mutableListOf<ExerciseModel>()
    private lateinit var adapter: ExercisesAdapter
    private lateinit var viewModel: ExercisesViewModel
    private var isShowingOldExercises = false

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

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(ExercisesViewModel::class.java)

        // Set up RecyclerView
        adapter = ExercisesAdapter(exercises, ::onStartStopClick, ::onDeleteClick)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(context)
        exercisesRecyclerView.adapter = adapter

        // Observe exercises from ViewModel
        viewModel.exercises.observe(viewLifecycleOwner) { loadedExercises ->
            exercises.clear()
            exercises.addAll(loadedExercises)
            adapter.notifyDataSetChanged()
            Log.d("ExercisesFragment", "Exercises updated: ${loadedExercises.size} items")
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("ExercisesFragment", "Error: $it")
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Add Exercise button
        addExerciseButton.setOnClickListener {
            val exerciseName = exerciseNameEditText.text.toString().trim()
            if (exerciseName.isNotEmpty()) {
                val userId = getCurrentUserId()
                viewModel.addExercise(exerciseName, userId)
                exerciseNameEditText.text.clear()
                Toast.makeText(context, "Exercise added: $exerciseName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter an exercise name", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Back button with interface
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as? DashboardNavigator)?.resetDashboardUI() ?: Log.w("ExercisesFragment", "DashboardNavigator not implemented")
        }

        // Load existing exercises
        val userId = getCurrentUserId()
        Log.d("ExercisesFragment", "Loading exercises for userId: $userId")
        viewModel.loadExercises(userId)

        return view
    }

    private fun onStartStopClick(exercise: ExerciseModel) {
        exercise.isRunning = !exercise.isRunning
        viewModel.updateExercise(exercise)
        val action = if (exercise.isRunning) "started" else "stopped"
        Toast.makeText(context, "${exercise.name} $action", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(exercise: ExerciseModel) {
        viewModel.deleteExercise(exercise.id, exercise.userId)
        Toast.makeText(context, "${exercise.name} deleted", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentUserId(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}