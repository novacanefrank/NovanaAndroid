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
import com.example.novana.viewmodel.GoalsViewModel

class GoalsFragment : Fragment() {

    private lateinit var goalNameEditText: EditText
    private lateinit var addGoalButton: Button
    private lateinit var backButton: Button
    private lateinit var goalsRecyclerView: RecyclerView
    private val goals = mutableListOf<GoalsModel>()
    private lateinit var adapter: GoalsAdapter
    private lateinit var viewModel: GoalsViewModel
    private var isShowingOldGoals = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        // Initialize views
        goalNameEditText = view.findViewById(R.id.goalNameEditText)
        addGoalButton = view.findViewById(R.id.addGoalButton)
        backButton = view.findViewById(R.id.backButton)
        goalsRecyclerView = view.findViewById(R.id.goalsRecyclerView)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(GoalsViewModel::class.java)

        // Set up RecyclerView
        adapter = GoalsAdapter(goals, ::onCompleteClick, ::onDeleteClick)
        goalsRecyclerView.layoutManager = LinearLayoutManager(context)
        goalsRecyclerView.adapter = adapter

        // Observe goals from ViewModel
        viewModel.goals.observe(viewLifecycleOwner) { loadedGoals ->
            goals.clear()
            goals.addAll(loadedGoals)
            adapter.notifyDataSetChanged()
            Log.d("GoalsFragment", "Goals updated: ${loadedGoals.size} items")
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("GoalsFragment", "Error: $it")
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Add Goal button
        addGoalButton.setOnClickListener {
            val goalName = goalNameEditText.text.toString().trim()
            if (goalName.isNotEmpty()) {
                val userId = getCurrentUserId()
                val newGoal = GoalsModel(name = goalName, userId = userId) // Let Firestore generate id
                viewModel.addGoal(goalName, userId)
                goalNameEditText.text.clear()
                Toast.makeText(context, "Goal added: $goalName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter a goal", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Back button with interface
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as? DashboardNavigator)?.resetDashboardUI() ?: Log.w("GoalsFragment", "DashboardNavigator not implemented")
        }

        // Load existing goals
        val userId = getCurrentUserId()
        Log.d("GoalsFragment", "Loading goals for userId: $userId")
        viewModel.loadGoals(userId)

        return view
    }

    private fun onCompleteClick(goal: GoalsModel) {
        goal.isCompleted = !goal.isCompleted
        viewModel.updateGoal(goal)
        val status = if (goal.isCompleted) "completed" else "marked incomplete"
        Toast.makeText(context, "${goal.name} $status", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(goal: GoalsModel) {
        viewModel.deleteGoal(goal.id, goal.userId)
        Toast.makeText(context, "${goal.name} deleted", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentUserId(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}