package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ComplaintRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ComplaintViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(db.userDao())
    private val complaintRepository = ComplaintRepository(db.complaintDao())
    private val notificationRepository = NotificationRepository(db.notificationDao())

    // Auth State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Live notifications StateFlow
    private val _userNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val userNotifications: StateFlow<List<Notification>> = _userNotifications.asStateFlow()

    private var notificationJob: kotlinx.coroutines.Job? = null

    // Users List (for Admin management)
    val allUsers: StateFlow<List<User>> = userRepository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Complaints Listing
    private val _citizensComplaints = MutableStateFlow<List<Complaint>>(emptyList())
    val citizensComplaints: StateFlow<List<Complaint>> = _citizensComplaints.asStateFlow()

    private val _allComplaints = MutableStateFlow<List<Complaint>>(emptyList())
    val allComplaints: StateFlow<List<Complaint>> = _allComplaints.asStateFlow()

    // Selection
    private val _selectedComplaint = MutableStateFlow<Complaint?>(null)
    val selectedComplaint: StateFlow<Complaint?> = _selectedComplaint.asStateFlow()

    init {
        // Pre-populate DB with default accounts and sample data if empty
        viewModelScope.launch {
            seedDefaultData()
            observeComplaints()
        }
    }

    private fun observeComplaints() {
        // Observe all complaints
        complaintRepository.getAllComplaints()
            .onEach { list ->
                _allComplaints.value = list
                // If user is logged in as citizen, filter or update theirs
                _currentUser.value?.let { user ->
                    if (user.role == UserRole.MASYARAKAT) {
                        _citizensComplaints.value = list.filter { it.userId == user.id }
                    }
                }
            }
            .launchIn(viewModelScope)
            
        // Observe current user changes to reload citizen lists & notifications
        _currentUser.onEach { user ->
            if (user != null && user.role == UserRole.MASYARAKAT) {
                _citizensComplaints.value = _allComplaints.value.filter { it.userId == user.id }
            } else {
                _citizensComplaints.value = emptyList()
            }

            notificationJob?.cancel()
            if (user != null) {
                notificationJob = viewModelScope.launch {
                    val roleName = when (user.role) {
                        UserRole.ADMIN -> "PETUGAS"
                        UserRole.PETUGAS -> "PETUGAS"
                        UserRole.MASYARAKAT -> "MASYARAKAT"
                    }
                    notificationRepository.getNotificationsForUser(user.id, roleName).collect { list ->
                        _userNotifications.value = list
                    }
                }
            } else {
                _userNotifications.value = emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun seedDefaultData() {
        // 1. Seed accounts
        val defaultAdmin = userRepository.getUserByUsername("admin")
        if (defaultAdmin == null) {
            userRepository.registerUser(
                User(
                    name = "H. Muhammad Yusuf (Admin)",
                    username = "admin",
                    password = "admin",
                    role = UserRole.ADMIN,
                    phone = "0811223344"
                )
            )
        }

        var officerId: Long = 0
        val defaultOfficer = userRepository.getUserByUsername("petugas")
        if (defaultOfficer == null) {
            officerId = userRepository.registerUser(
                User(
                    name = "Budi Santoso (Petugas PUPR)",
                    username = "petugas",
                    password = "petugas",
                    role = UserRole.PETUGAS,
                    phone = "0812233445"
                )
            )
        } else {
            officerId = defaultOfficer.id
        }

        var citizenId: Long = 0
        val defaultCitizen = userRepository.getUserByUsername("warga")
        if (defaultCitizen == null) {
            citizenId = userRepository.registerUser(
                User(
                    name = "Rangga Zeed (Warga Cipedes)",
                    username = "warga",
                    password = "warga",
                    role = UserRole.MASYARAKAT,
                    phone = "081234567890"
                )
            )
        } else {
            citizenId = defaultCitizen.id
        }

        // Checking complaints table
        val existingComplaints = complaintRepository.getAllComplaints().firstOrNull() ?: emptyList()
        if (existingComplaints.isEmpty()) {
            // Seed sample complaints
            complaintRepository.createComplaint(
                Complaint(
                    userId = citizenId,
                    authorName = "Rangga Zeed (Warga Cipedes)",
                    title = "Jalan Berlubang Parah di Jl. Indihiang",
                    description = "Ada lubang besar berdiameter 1 meter dengan kedalaman sekitar 15cm di lajur kiri jalan dekat Terminal Indihiang. Sering membuat pengendara roda dua terjatuh mendadak jika hujan deras turun karena lubang tertutup genangan air secara penuh. Mohon segera ditindaklanjuti untuk ditambal.",
                    category = "Jalan Rusak",
                    location = "Kecamatan Indihiang",
                    status = ComplaintStatus.MENUNGGU,
                    photoName = "jalan_rusak",
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 5) // 5 hours ago
                )
            )

            complaintRepository.createComplaint(
                Complaint(
                    userId = citizenId,
                    authorName = "Rangga Zeed (Warga Cipedes)",
                    title = "Selokan Air Mampet di Pasar Cikurubuk",
                    description = "Saluran air selokan utama di Blok B Pasar Cikurubuk tersumbat berat oleh tumpukan sampah plastik dan limbah sayuran. Hal ini menyebabkan air hitam berbau busuk meluap membanjiri jalanan lorong lapak pedagang setiap kali diguyur hujan lebat. Sangat mengganggu kesehatan pedagang dan pembeli.",
                    category = "Drainase Tersumbat",
                    location = "Kecamatan Mangkubumi",
                    status = ComplaintStatus.SELESAI,
                    photoName = "selokan_mampet",
                    timestamp = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 4), // 4 days ago
                    feedback = "Kami laporkan bahwa petugas Dinas Lingkungan Hidup bekerja sama dengan pengelola Pasar Cikurubuk telah membersihkan sedimen lumpur dan sumbatan sampah pada tanggal 23 Mei 2026. Aliran air selokan kini berjalan normal.",
                    feedbackAuthor = "Budi Santoso (Petugas PUPR)",
                    feedbackTimestamp = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 3) // 3 days ago
                )
            )

            complaintRepository.createComplaint(
                Complaint(
                    userId = citizenId,
                    authorName = "Rangga Zeed (Warga Cipedes)",
                    title = "Lampu Penerangan Mati di Jl. HZ. Mustofa",
                    description = "Penerangan jalan umum (PJU) sepanjang jalur pedestrian depan Plaza Asia hingga persimpangan lampu merah Padayungan mati total sebanyak 5 tiang berturut-turut. Kondisi menjadi sangat gelap gulita di malam hari, rawan aksi begal, penjambretan, dan juga membahayakan pejalan kaki.",
                    category = "Lampu Penerangan Jalan Umum Mati",
                    location = "Kecamatan Cihideung",
                    status = ComplaintStatus.PROSES,
                    photoName = "pju_mati",
                    timestamp = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 2) // 2 days ago
                )
            )
        }
    }

    // Actions
    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            if (username.isBlank() || password.isBlank()) {
                _authError.value = "Username dan password tidak boleh kosong!"
                onResult(false)
                return@launch
            }
            val user = userRepository.getUserByUsername(username)
            if (user == null) {
                _authError.value = "Username tidak terdaftar!"
                onResult(false)
            } else if (user.password != password) {
                _authError.value = "Password salah!"
                onResult(false)
            } else {
                _currentUser.value = user
                onResult(true)
            }
        }
    }

    fun register(name: String, username: String, password: String, phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            if (name.isBlank() || username.isBlank() || password.isBlank() || phone.isBlank()) {
                _authError.value = "Semua bidang registrasi wajib diisi!"
                onResult(false)
                return@launch
            }
            if (username.contains(" ")) {
                _authError.value = "Username tidak boleh mengandung spasi!"
                onResult(false)
                return@launch
            }
            val existing = userRepository.getUserByUsername(username)
            if (existing != null) {
                _authError.value = "Username ini sudah terdaftar!"
                onResult(false)
                return@launch
            }

            val newUser = User(
                name = name,
                username = username.lowercase(),
                password = password,
                role = UserRole.MASYARAKAT,
                phone = phone
            )
            val newId = userRepository.registerUser(newUser)
            if (newId > 0) {
                _currentUser.value = newUser.copy(id = newId)
                onResult(true)
            } else {
                _authError.value = "Format registrasi salah, silakan coba lagi!"
                onResult(false)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _citizensComplaints.value = emptyList()
        _selectedComplaint.value = null
        _authError.value = null
    }

    // Complaints actions
    fun submitComplaint(title: String, description: String, category: String, location: String, photoName: String, onResult: (Boolean) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false)
            return
        }
        if (title.isBlank() || description.isBlank() || category.isBlank() || location.isBlank()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val newComplaint = Complaint(
                userId = user.id,
                authorName = user.name,
                title = title,
                description = description,
                category = category,
                location = location,
                status = ComplaintStatus.MENUNGGU,
                photoName = photoName,
                timestamp = System.currentTimeMillis()
            )
            val resultId = complaintRepository.createComplaint(newComplaint)
            if (resultId > 0) {
                // Automatic notification for officers
                notificationRepository.insert(
                    Notification(
                        userId = 0, // Broad to PETUGAS role
                        roleType = "PETUGAS",
                        title = "Laporan Baru Ditambahkan",
                        message = "Laporan baru mengenai '$category' di '$location' dilaporkan oleh ${user.name}."
                    )
                )
            }
            onResult(resultId > 0)
        }
    }

    fun selectComplaint(complaint: Complaint) {
        _selectedComplaint.value = complaint
    }

    fun clearSelectedComplaint() {
        _selectedComplaint.value = null
    }

    fun updateComplaintStatus(complaintId: Long, newStatus: ComplaintStatus, feedbackText: String?, onResult: (Boolean) -> Unit) {
        val user = _currentUser.value
        if (user == null || (user.role != UserRole.PETUGAS && user.role != UserRole.ADMIN)) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val original = complaintRepository.getComplaintRaw(complaintId)
            if (original != null) {
                val updated = original.copy(
                    status = newStatus,
                    feedback = if (!feedbackText.isNullOrBlank()) feedbackText else original.feedback,
                    feedbackAuthor = if (!feedbackText.isNullOrBlank()) user.name else original.feedbackAuthor,
                    feedbackTimestamp = if (!feedbackText.isNullOrBlank()) System.currentTimeMillis() else original.feedbackTimestamp
                )
                complaintRepository.updateComplaint(updated)
                // update selected too if active
                if (_selectedComplaint.value?.id == complaintId) {
                    _selectedComplaint.value = updated
                }
                
                // Automatic notification for citizen
                notificationRepository.insert(
                    Notification(
                        userId = original.userId,
                        roleType = "MASYARAKAT",
                        title = "Status Laporan Anda Diperbarui",
                        message = "Laporan '${original.title}' Anda kini berstatus [${newStatus.displayName}].${if (!feedbackText.isNullOrBlank()) " Tanggapan petugas: \"$feedbackText\"." else ""}"
                    )
                )
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun deleteComplaint(complaintId: Long, onResult: (Boolean) -> Unit) {
        val user = _currentUser.value
        if (user == null || user.role != UserRole.ADMIN) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            val original = complaintRepository.getComplaintRaw(complaintId)
            if (original != null) {
                complaintRepository.deleteComplaint(original)
                if (_selectedComplaint.value?.id == complaintId) {
                    _selectedComplaint.value = null
                }
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun deleteUser(targetUser: User, onResult: (Boolean) -> Unit) {
        val user = _currentUser.value
        if (user == null || user.role != UserRole.ADMIN) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            if (targetUser.id == user.id) {
                onResult(false) // Can't delete self
                return@launch
            }
            userRepository.deleteUser(targetUser)
            onResult(true)
        }
    }

    fun changeUserRole(targetUser: User, newRole: UserRole, onResult: (Boolean) -> Unit) {
        val user = _currentUser.value
        if (user == null || user.role != UserRole.ADMIN) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            if (targetUser.id == user.id) {
                onResult(false) // Can't change own role
                return@launch
            }
            val updated = targetUser.copy(role = newRole)
            userRepository.updateUser(updated)
            onResult(true)
        }
    }

    // Notifications API operations
    fun markAllNotificationsAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val roleName = when (user.role) {
                UserRole.ADMIN -> "PETUGAS"
                UserRole.PETUGAS -> "PETUGAS"
                UserRole.MASYARAKAT -> "MASYARAKAT"
            }
            notificationRepository.markAllAsRead(user.id, roleName)
        }
    }

    fun markNotificationAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun clearAllNotifications() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val roleName = when (user.role) {
                UserRole.ADMIN -> "PETUGAS"
                UserRole.PETUGAS -> "PETUGAS"
                UserRole.MASYARAKAT -> "MASYARAKAT"
            }
            notificationRepository.clearAll(user.id, roleName)
        }
    }

    // Simulated Export/Print Report as CSV
    fun generateReportData(): String {
        val list = _allComplaints.value
        val sb = StringBuilder()
        sb.append("===== PERSATUAN LAPORAN PENGADUAN KOTA TASIKMALAYA =====\n")
        sb.append("Diekspor pada: 2026-05-24\n\n")
        sb.append("ID | Tanggal | Pelapor | Kategori | Lokasi | Judul | Status\n")
        sb.append("---------------------------------------------------------------------------------\n")
        list.forEach { complaint ->
            val date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(complaint.timestamp))
            sb.append("${complaint.id} | $date | ${complaint.authorName} | ${complaint.category} | ${complaint.location} | ${complaint.title} | ${complaint.status.displayName}\n")
        }
        return sb.toString()
    }
}

// Simple extension helper as a substitute for importing full library builders
private fun <T> Flow<T>.launchIn(scope: kotlinx.coroutines.CoroutineScope): kotlinx.coroutines.Job = scope.launch {
    collect()
}
