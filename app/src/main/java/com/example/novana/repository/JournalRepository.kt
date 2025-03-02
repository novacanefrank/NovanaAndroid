package com.example.novana.repository


import com.example.novana.ui.fragment.JournalEntryModel
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    suspend fun addEntry(entry: JournalEntryModel, userId: String): Result<Unit>
    suspend fun updateEntry(entry: JournalEntryModel): Result<Unit>
    suspend fun deleteEntry(entryId: Int): Result<Unit>
    fun getEntries(userId: String): Flow<List<JournalEntryModel>>
}