package com.example.novana.repository

import com.example.novana.ui.fragment.GoalsModel
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    suspend fun addGoal(goal: GoalsModel, userId: String): Result<Unit>
    suspend fun updateGoal(goal: GoalsModel): Result<Unit>
    suspend fun deleteGoal(goalId: Int, userId: String): Result<Unit>
    fun getGoals(userId: String): Flow<List<GoalsModel>>
}