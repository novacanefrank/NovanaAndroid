package com.example.novana.repository.impl

import android.util.Log
import com.example.novana.ui.fragment.ExerciseModel
import com.example.novana.repository.ExercisesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ExercisesRepositoryImpl : ExercisesRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val exercisesCollection = firestore.collection("exercises")

    override suspend fun addExercise(exercise: ExerciseModel, userId: String): Result<Unit> {
        return try {
            val updatedExercise = exercise.copy(userId = userId)
            val documentRef = exercisesCollection.add(updatedExercise.toMap()).await()
            Log.d("ExercisesRepository", "Added exercise with document ID: ${documentRef.id}, userId: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ExercisesRepository", "Add exercise error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateExercise(exercise: ExerciseModel): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user"))
            exercisesCollection.whereEqualTo("id", exercise.id)
                .whereEqualTo("userId", exercise.userId)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.update(exercise.toMap())
                ?.await()
            Log.d("ExercisesRepository", "Updated exercise with id: ${exercise.id}, userId: ${exercise.userId}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ExercisesRepository", "Update exercise error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteExercise(exerciseId: Int, userId: String): Result<Unit> {
        return try {
            exercisesCollection.whereEqualTo("id", exerciseId)
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.delete()
                ?.await()
            Log.d("ExercisesRepository", "Deleted exercise with id: $exerciseId, userId: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ExercisesRepository", "Delete exercise error: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getExercises(userId: String): Flow<List<ExerciseModel>> {
        return flow {
            try {
                val snapshot = exercisesCollection.whereEqualTo("userId", userId).get().await()
                val exercises = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: 0
                    val name = doc.getString("name") ?: ""
                    val isRunning = doc.getBoolean("isRunning") ?: false
                    val userIdFromDoc = doc.getString("userId") ?: ""
                    val timestamp = doc.getString("timestamp") ?: ""
                    if (name.isNotEmpty()) {
                        ExerciseModel(id, name, isRunning, userIdFromDoc, timestamp)
                    } else {
                        null
                    }
                }
                Log.d("ExercisesRepository", "Fetched ${exercises.size} exercises for userId: $userId")
                emit(exercises)
            } catch (e: Exception) {
                Log.e("ExercisesRepository", "Get exercises error: ${e.message}")
                emit(emptyList())
            }
        }
    }
}