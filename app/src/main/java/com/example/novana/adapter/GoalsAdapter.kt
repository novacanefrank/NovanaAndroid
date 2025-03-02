package com.example.novana.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novana.R

class GoalsAdapter(
    private val goals: MutableList<GoalsModel>,
    private val onCompleteClick: (GoalsModel) -> Unit,
    private val onDeleteClick: (GoalsModel) -> Unit
) : RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder>() {

    class GoalsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goalName: TextView = itemView.findViewById(R.id.goalName)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goals, parent, false)
        return GoalsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalsViewHolder, position: Int) {
        val goal = goals[position]
        holder.goalName.text = goal.name
        holder.completeButton.text = if (goal.isCompleted) "Completed" else "Complete"
        holder.completeButton.setOnClickListener { onCompleteClick(goal) }
        holder.deleteButton.setOnClickListener { onDeleteClick(goal) }
    }

    override fun getItemCount(): Int = goals.size

    fun removeGoal(goal: GoalsModel) {
        val position = goals.indexOf(goal)
        if (position != -1) {
            goals.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}