package com.example.novana.ui.fragment

data class JournalEntryModel(
    val id: Int,
    var text: String,
    val timestamp: String = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())
)