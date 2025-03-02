package com.example.novana.ui.fragment

import java.text.SimpleDateFormat
import java.util.Date

data class GoalsModel(
    val id: Int = 0,
    var name: String,
    var isCompleted: Boolean = false,
    val userId: String = "",
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
) {

    // Default constructor for Firestore
    constructor() : this(0, "", false, "", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

    // Convert to Map for Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "isCompleted" to isCompleted,
            "userId" to userId,
            "timestamp" to timestamp
        )
    }
}