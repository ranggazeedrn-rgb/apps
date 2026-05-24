package com.example.data.repository

import com.example.data.database.ComplaintDao
import com.example.data.model.Complaint
import kotlinx.coroutines.flow.Flow

class ComplaintRepository(private val complaintDao: ComplaintDao) {
    fun getAllComplaints(): Flow<List<Complaint>> = complaintDao.getAllComplaintsFlow()

    fun getComplaintsByUser(userId: Long): Flow<List<Complaint>> = complaintDao.getComplaintsByUserFlow(userId)

    fun getComplaintById(id: Long): Flow<Complaint?> = complaintDao.getComplaintByIdFlow(id)

    suspend fun getComplaintRaw(id: Long): Complaint? = complaintDao.getComplaintById(id)

    suspend fun createComplaint(complaint: Complaint): Long = complaintDao.insertComplaint(complaint)

    suspend fun updateComplaint(complaint: Complaint) {
        complaintDao.updateComplaint(complaint)
    }

    suspend fun deleteComplaint(complaint: Complaint) {
        complaintDao.deleteComplaint(complaint)
    }
}
