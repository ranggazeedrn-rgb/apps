package com.example.data.repository

import com.example.data.database.NotificationDao
import com.example.data.model.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsForUser(userId: Long, roleType: String): Flow<List<Notification>> {
        return notificationDao.getNotificationsForUserFlow(userId, roleType)
    }

    suspend fun insert(notification: Notification): Long {
        return notificationDao.insertNotification(notification)
    }

    suspend fun markAllAsRead(userId: Long, roleType: String) {
        notificationDao.markAllAsReadForUser(userId, roleType)
    }

    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    suspend fun delete(notification: Notification) {
        notificationDao.deleteNotification(notification)
    }

    suspend fun clearAll(userId: Long, roleType: String) {
        notificationDao.clearAllNotificationsForUser(userId, roleType)
    }
}
