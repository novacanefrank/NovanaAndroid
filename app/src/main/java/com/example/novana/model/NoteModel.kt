package com.example.novana.ui.activity

data class NoteModel(
    val id: Int,
    var text: String,
    val timestamp: String = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())
)