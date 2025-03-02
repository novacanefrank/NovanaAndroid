package com.example.novana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.novana.data.model.UserModel
import com.example.novana.repository.impl.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepositoryImpl()

    private val _user = MutableStateFlow<UserModel?>(null)
    val user = _user.asLiveData()  // Convert StateFlow to LiveData

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asLiveData()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asLiveData()

    fun registerUser(email: String, password: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registerUser(email, password, username)
            _isLoading.value = false
            result.onSuccess { user ->
                _user.value = user
                _errorMessage.value = null
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.loginUser(email, password)
            _isLoading.value = false
            result.onSuccess { user ->
                _user.value = user
                _errorMessage.value = null
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { user ->
                _user.value = user
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null
        }
    }
}
