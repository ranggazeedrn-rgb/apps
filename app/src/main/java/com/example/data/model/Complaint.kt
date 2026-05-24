package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color

enum class ComplaintStatus(val displayName: String) {
    MENUNGGU("Menunggu"),
    DIVALIDASI("Divalidasi"),
    PROSES("Diproses"),
    SELESAI("Selesai"),
    DITOLAK("Ditolak");
    
    fun getStatusColor(): Color {
        return when (this) {
            MENUNGGU -> Color(0xFFF59E0B) // Amber
            DIVALIDASI -> Color(0xFF3B82F6) // Blue
            PROSES -> Color(0xFF8B5CF6) // Violet/Purple
            SELESAI -> Color(0xFF10B981) // Emerald Green
            DITOLAK -> Color(0xFFEF4444) // Red
        }
    }
}

@Entity(tableName = "complaints")
data class Complaint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val authorName: String,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val status: ComplaintStatus = ComplaintStatus.MENUNGGU,
    val timestamp: Long = System.currentTimeMillis(),
    val photoName: String = "", // Holds preset photo identifier or empty
    val feedback: String? = null,
    val feedbackAuthor: String? = null,
    val feedbackTimestamp: Long? = null
)
