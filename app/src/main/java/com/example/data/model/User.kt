package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    MASYARAKAT,
    PETUGAS,
    ADMIN
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val username: String,
    val password: String,
    val role: UserRole,
    val phone: String = ""
)
