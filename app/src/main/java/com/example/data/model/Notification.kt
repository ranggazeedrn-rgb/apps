package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // Specific citizen ID, or 0 if general to a role
    val roleType: String = "MASYARAKAT", // "MASYARAKAT" or "PETUGAS"
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
