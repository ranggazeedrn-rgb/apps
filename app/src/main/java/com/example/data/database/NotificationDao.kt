package com.example.data.database

import androidx.room.*
import com.example.data.model.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId OR (userId = 0 AND roleType = :roleType) ORDER BY timestamp DESC")
    fun getNotificationsForUserFlow(userId: Long, roleType: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR (userId = 0 AND roleType = :roleType)")
    suspend fun markAllAsReadForUser(userId: Long, roleType: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Delete
    suspend fun deleteNotification(notification: Notification)

    @Query("DELETE FROM notifications WHERE userId = :userId OR (userId = 0 AND roleType = :roleType)")
    suspend fun clearAllNotificationsForUser(userId: Long, roleType: String)
}
