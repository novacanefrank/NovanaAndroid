package com.example.novana.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novana.ui.fragment.ExerciseModel
import com.example.novana.repository.ExercisesRepository
import com.example.novana.repository.impl.ExercisesRepositoryImpl
import kotlinx.coroutines.launch

class ExercisesViewModel : ViewModel() {

    private val repository = ExercisesRepositoryImpl()
    private val _exercises = MutableLiveData<List<ExerciseModel>>(emptyList())
    val exercises: LiveData<List<ExerciseModel>> get() = _exercises

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadExercises(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.getExercises(userId).collect { exercises ->
                    _exercises.value = exercises
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun addExercise(name: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val exercise = ExerciseModel(name = name, userId = userId) // Omit id, let Firestore generate it
            val result = repository.addExercise(exercise, userId)
            if (result.isSuccess) {
                loadExercises(userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun updateExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.updateExercise(exercise)
            if (result.isSuccess) {
                loadExercises(exercise.userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun deleteExercise(exerciseId: Int, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.deleteExercise(exerciseId, userId)
            if (result.isSuccess) {
                loadExercises(userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
}