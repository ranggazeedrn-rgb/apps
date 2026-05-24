package com.example.data.database

import android.content.Context
import androidx.room.*
import com.example.data.model.Complaint
import com.example.data.model.ComplaintStatus
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.Notification

class Converters {
    @TypeConverter
    fun fromStatus(status: ComplaintStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ComplaintStatus = try {
        ComplaintStatus.valueOf(value)
    } catch (e: Exception) {
        ComplaintStatus.MENUNGGU
    }

    @TypeConverter
    fun fromRole(role: UserRole): String = role.name

    @TypeConverter
    fun toRole(value: String): UserRole = try {
        UserRole.valueOf(value)
    } catch (e: Exception) {
        UserRole.MASYARAKAT
    }
}

@Database(entities = [User::class, Complaint::class, Notification::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun complaintDao(): ComplaintDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasik_pengaduan_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
