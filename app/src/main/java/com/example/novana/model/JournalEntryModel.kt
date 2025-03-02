package com.example.novana.ui.fragment

import java.text.SimpleDateFormat
import java.util.Date

data class JournalEntryModel(
    val id: Int,
    var text: String,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
    val userId: String = ""
) {

    // Default constructor for Firestore
    constructor() : this(0, "", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()), "")

    // Convert to Map for Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "text" to text,
            "timestamp" to timestamp,
            "userId" to userId
        )
    }
}