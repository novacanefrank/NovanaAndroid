package com.example.novana.repository

import com.example.novana.ui.fragment.ExerciseModel
import kotlinx.coroutines.flow.Flow

interface ExercisesRepository {
    suspend fun addExercise(exercise: ExerciseModel, userId: String): Result<Unit>
    suspend fun updateExercise(exercise: ExerciseModel): Result<Unit>
    suspend fun deleteExercise(exerciseId: Int, userId: String): Result<Unit>
    fun getExercises(userId: String): Flow<List<ExerciseModel>>
}