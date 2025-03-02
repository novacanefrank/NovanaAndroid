package com.example.novana.data.model

import java.util.Date

data class UserModel(
    val id: String = "", // Firebase UID or custom ID
    val username: String = "",
    val email: String = "",
    val password: String = "", // Note: Avoid storing plain passwords; use securely
    val createdAt: Date = Date(), // Timestamp of user creation
    val isActive: Boolean = true // Optional: Track user status
) {

    // Default constructor required for Firebase
    constructor() : this("", "", "", "", Date(), true)

    // Optional: To convert to a Map for Firebase
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "username" to username,
            "email" to email,
            "password" to password, // Avoid storing password in production
            "createdAt" to createdAt,
            "isActive" to isActive
        )
    }
}