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

class GoalsFragment : Fragment() {

    private lateinit var goalNameEditText: EditText
    private lateinit var addGoalButton: Button
    private lateinit var backButton: Button
    private lateinit var goalsRecyclerView: RecyclerView
    private val goals = mutableListOf<GoalsModel>()
    private var nextId = 0
    private lateinit var adapter: GoalsAdapter

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

        // Set up RecyclerView
        adapter = GoalsAdapter(goals, ::onCompleteClick, ::onDeleteClick)
        goalsRecyclerView.layoutManager = LinearLayoutManager(context)
        goalsRecyclerView.adapter = adapter

        // Set click listener for Add Goal button
        addGoalButton.setOnClickListener {
            val goalName = goalNameEditText.text.toString().trim()
            if (goalName.isNotEmpty()) {
                val newGoal = GoalsModel(nextId++, goalName)
                goals.add(newGoal)
                adapter.notifyDataSetChanged()
                goalNameEditText.text.clear()
                Toast.makeText(context, "Goal added: $goalName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter a goal", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for Back button
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as DashboardActivity).resetDashboardUI()
        }

        return view
    }

    private fun onCompleteClick(goal: GoalsModel) {
        goal.isCompleted = !goal.isCompleted
        adapter.notifyDataSetChanged()
        val status = if (goal.isCompleted) "completed" else "marked incomplete"
        Toast.makeText(context, "${goal.name} $status", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(goal: GoalsModel) {
        goals.remove(goal)
        adapter.removeGoal(goal)
        Toast.makeText(context, "${goal.name} deleted", Toast.LENGTH_SHORT).show()
    }
}