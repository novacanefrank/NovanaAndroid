package com.example.novana.repository.impl

import android.util.Log
import com.example.novana.ui.fragment.JournalEntryModel
import com.example.novana.repository.JournalRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class JournalRepositoryImpl : JournalRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val journalCollection = firestore.collection("journal_entries")

    override suspend fun addEntry(entry: JournalEntryModel, userId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user"))
            Log.d("JournalRepository", "Attempting to add entry for userId: $currentUserId")
            val updatedEntry = entry.copy(userId = userId)
            val documentRef = journalCollection.add(updatedEntry.toMap()).await()
            Log.d("JournalRepository", "Added entry with document ID: ${documentRef.id}, userId: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JournalRepository", "Add entry error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateEntry(entry: JournalEntryModel): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user"))
            journalCollection.whereEqualTo("id", entry.id)
                .whereEqualTo("userId", entry.userId)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.update(entry.toMap())
                ?.await()
            Log.d("JournalRepository", "Updated entry with id: ${entry.id}, userId: ${entry.userId}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JournalRepository", "Update entry error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteEntry(entryId: Int): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user"))
            journalCollection.whereEqualTo("id", entryId)
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.delete()
                ?.await()
            Log.d("JournalRepository", "Deleted entry with id: $entryId, userId: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JournalRepository", "Delete entry error: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getEntries(userId: String): Flow<List<JournalEntryModel>> {
        return flow {
            try {
                val snapshot = journalCollection.whereEqualTo("userId", userId).get().await()
                val entries = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: 0
                    val text = doc.getString("text") ?: ""
                    val timestamp = doc.getString("timestamp") ?: ""
                    val entryUserId = doc.getString("userId") ?: ""
                    if (id != 0 || text.isNotEmpty()) {
                        JournalEntryModel(id, text, timestamp, entryUserId)
                    } else {
                        null
                    }
                }
                Log.d("JournalRepository", "Fetched ${entries.size} entries for userId: $userId")
                emit(entries)
            } catch (e: Exception) {
                Log.e("JournalRepository", "Get entries error: ${e.message}")
                emit(emptyList())
            }
        }
    }
}