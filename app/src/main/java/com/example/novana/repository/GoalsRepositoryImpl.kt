package com.example.novana.repository.impl

import android.util.Log
import com.example.novana.ui.fragment.GoalsModel
import com.example.novana.repository.GoalsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class GoalsRepositoryImpl : GoalsRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val goalsCollection = firestore.collection("goals")

    override suspend fun addGoal(goal: GoalsModel, userId: String): Result<Unit> {
        return try {
            val updatedGoal = goal.copy(userId = userId) // id will be ignored, Firestore generates it
            val documentRef = goalsCollection.add(updatedGoal.toMap()).await()
            // Optionally, you could update the goal.id with the document ID if needed
            // goal.id = documentRef.id.hashCode() // Uncomment if you want to sync local id
            Log.d("GoalsRepository", "Added goal with document ID: ${documentRef.id}, userId: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("GoalsRepository", "Add goal error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: GoalsModel): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user"))
            goalsCollection.whereEqualTo("id", goal.id)
                .whereEqualTo("userId", goal.userId)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.update(goal.toMap())
                ?.await()
            Log.d("GoalsRepository", "Updated goal with id: ${goal.id}, userId: ${goal.userId}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("GoalsRepository", "Update goal error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteGoal(goalId: Int, userId: String): Result<Unit> {
        return try {
            goalsCollection.whereEqualTo("id", goalId)
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.delete()
                ?.await()
            Log.d("GoalsRepository", "Deleted goal with id: $goalId, userId: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("GoalsRepository", "Delete goal error: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getGoals(userId: String): Flow<List<GoalsModel>> {
        return flow {
            try {
                val snapshot = goalsCollection.whereEqualTo("userId", userId).get().await()
                val goals = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: 0 // Fallback if id isn't set
                    val name = doc.getString("name") ?: ""
                    val isCompleted = doc.getBoolean("isCompleted") ?: false
                    val userIdFromDoc = doc.getString("userId") ?: ""
                    val timestamp = doc.getString("timestamp") ?: ""
                    if (name.isNotEmpty()) {
                        GoalsModel(id, name, isCompleted, userIdFromDoc, timestamp)
                    } else {
                        null
                    }
                }
                Log.d("GoalsRepository", "Fetched ${goals.size} goals for userId: $userId")
                emit(goals)
            } catch (e: Exception) {
                Log.e("GoalsRepository", "Get goals error: ${e.message}")
                emit(emptyList())
            }
        }
    }
}