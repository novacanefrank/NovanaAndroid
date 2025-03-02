package com.example.novana.repository.impl

import android.util.Log
import com.example.novana.data.model.UserModel
import com.example.novana.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class UserRepositoryImpl : UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun registerUser(email: String, password: String, username: String): Result<UserModel> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = UserModel(
                    id = firebaseUser.uid,
                    username = username,
                    email = email,
                    createdAt = Date()
                )
                saveUserData(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: User is null"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<UserModel> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = fetchUserData(firebaseUser.uid) ?: UserModel(
                    id = firebaseUser.uid,
                    email = email
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: User is null"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Login error: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<UserModel?> {
        return flow {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val user = fetchUserData(currentUser.uid)
                emit(user)
            } else {
                emit(null)
            }
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("UserRepository", "Logout error: ${e.message}")
        }
    }

    override suspend fun saveUserData(user: UserModel) {
        try {
            firestore.collection("users")
                .document(user.id)
                .set(user.toMap())
                .await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Save user data error: ${e.message}")
        }
    }

    private suspend fun fetchUserData(uid: String): UserModel? {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            snapshot.toObject(UserModel::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Fetch user data error: ${e.message}")
            null
        }
    }
}