package com.example.novana.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novana.ui.fragment.GoalsModel
import com.example.novana.repository.GoalsRepository
import com.example.novana.repository.impl.GoalsRepositoryImpl
import kotlinx.coroutines.launch

class GoalsViewModel : ViewModel() {

    private val repository = GoalsRepositoryImpl()
    private val _goals = MutableLiveData<List<GoalsModel>>(emptyList())
    val goals: LiveData<List<GoalsModel>> get() = _goals

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadGoals(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.getGoals(userId).collect { goals ->
                    _goals.value = goals
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun addGoal(name: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val goal = GoalsModel(id = System.currentTimeMillis().toInt(), name = name, userId = userId)
            val result = repository.addGoal(goal, userId)
            if (result.isSuccess) {
                loadGoals(userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun updateGoal(goal: GoalsModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.updateGoal(goal)
            if (result.isSuccess) {
                loadGoals(goal.userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun deleteGoal(goalId: Int, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.deleteGoal(goalId, userId)
            if (result.isSuccess) {
                loadGoals(userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
}