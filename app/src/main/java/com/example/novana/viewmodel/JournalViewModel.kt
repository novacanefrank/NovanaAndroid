package com.example.novana.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novana.repository.JournalRepository
import com.example.novana.repository.impl.JournalRepositoryImpl
import com.example.novana.ui.fragment.JournalEntryModel
import kotlinx.coroutines.launch

class JournalViewModel : ViewModel() {

    private val repository = JournalRepositoryImpl()
    private val _journalEntries = MutableLiveData<List<JournalEntryModel>>(emptyList())
    val journalEntries: LiveData<List<JournalEntryModel>> get() = _journalEntries

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadEntries(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.getEntries(userId).collect { entries ->
                    _journalEntries.value = entries
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun addEntry(entry: JournalEntryModel, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.addEntry(entry, userId)
            if (result.isSuccess) {
                loadEntries(userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun updateEntry(entry: JournalEntryModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.updateEntry(entry)
            if (result.isSuccess) {
                loadEntries(entry.userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun deleteEntry(entryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val userId = getCurrentUserId() // Assume this is available
            val result = repository.deleteEntry(entryId)
            if (result.isSuccess) {
                loadEntries(userId)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    private fun getCurrentUserId(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}