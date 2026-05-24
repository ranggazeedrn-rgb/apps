package com.example.data.database

import androidx.room.*
import com.example.data.model.Complaint
import com.example.data.model.ComplaintStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY timestamp DESC")
    fun getAllComplaintsFlow(): Flow<List<Complaint>>

    @Query("SELECT * FROM complaints WHERE userId = :userId ORDER BY timestamp DESC")
    fun getComplaintsByUserFlow(userId: Long): Flow<List<Complaint>>

    @Query("SELECT * FROM complaints WHERE id = :id LIMIT 1")
    fun getComplaintByIdFlow(id: Long): Flow<Complaint?>

    @Query("SELECT * FROM complaints WHERE id = :id LIMIT 1")
    suspend fun getComplaintById(id: Long): Complaint?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: Complaint): Long

    @Update
    suspend fun updateComplaint(complaint: Complaint)

    @Delete
    suspend fun deleteComplaint(complaint: Complaint)
}
