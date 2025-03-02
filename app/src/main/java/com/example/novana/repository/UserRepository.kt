package com.example.novana.repository

import com.example.novana.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(email: String, password: String, username: String): Result<UserModel>
    suspend fun loginUser(email: String, password: String): Result<UserModel>
    fun getCurrentUser(): Flow<UserModel?>
    suspend fun logout()
    suspend fun saveUserData(user: UserModel)
}