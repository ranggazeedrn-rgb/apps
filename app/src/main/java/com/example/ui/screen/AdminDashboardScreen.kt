package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Complaint
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.component.ComplaintListItemCard
import com.example.ui.component.NotificationsDialog
import androidx.compose.material.icons.filled.NotificationsActive
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ComplaintViewModel,
    onNavigateToComplaintDetail: (Complaint) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Complaints, 1 = User Management
    val user by viewModel.currentUser.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onDismissRequest = { showNotificationsDialog = false },
            onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
            onMarkRead = { id -> viewModel.markNotificationAsRead(id) },
            onClearAll = { viewModel.clearAllNotifications() }
        )
    }

    var showReportExportDialog by remember { mutableStateOf(false) }
    var showUserRoleDialog by remember { mutableStateOf<User?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TASIK",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "SIAP",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "SISTEM PENGENDALIAN ADUAN & AKUN",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF5B6060),
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { showNotificationsDialog = true },
                            modifier = Modifier.testTag("notification_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFBA1A1A),
                                            contentColor = Color.White
                                        ) {
                                            Text(text = unreadCount.toString(), fontSize = 9.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Pemberitahuan",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Keluar Aplikasi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE1E3E3))
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("admin_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                    label = { Text("Laporan Aduan") },
                    modifier = Modifier.testTag("tab_complaints")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ManageAccounts, contentDescription = null) },
                    label = { Text("Kelola Pengguna") },
                    modifier = Modifier.testTag("tab_users")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector Rendering
            when (selectedTab) {
                0 -> {
                    // TAB 0: Complaints & Export Report
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Horizontal Control Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("control_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Ekspor Rekap Laporan",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Cetak seluruh data aduan ke PDF/Excel",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }

                                Button(
                                    onClick = { showReportExportDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("cetak_laporan_button")
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cetak", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Complaints list
                        Text(
                            text = "Daftar Pengaduan Masuk (${allComplaints.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                        )

                        if (allComplaints.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tidak ada pengaduan masyarakat yang terdaftar.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(allComplaints) { complaint ->
                                    ComplaintListItemCard(
                                        complaint = complaint,
                                        onClick = {
                                            viewModel.selectComplaint(complaint)
                                            onNavigateToComplaintDetail(complaint)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 1: User Management (Add, change role, delete)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Kelola Hak Akses Pengguna",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ubah jabatan pengguna atau hapus akun demo warga di sini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Users ListView
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(allUsers) { u ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("user_item_${u.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = if (u.role == UserRole.ADMIN) Color(0xFFFEE2E2) else if (u.role == UserRole.PETUGAS) Color(0xFFE0E7FF) else Color(0xFFD1FAE5),
                                                    shape = RoundedCornerShape(100)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (u.role == UserRole.ADMIN) Icons.Default.Shield else Icons.Default.Group,
                                                contentDescription = null,
                                                tint = if (u.role == UserRole.ADMIN) Color(0xFFDC2626) else if (u.role == UserRole.PETUGAS) Color(0xFF3B82F6) else Color(0xFF059669),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = u.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Username: ${u.username}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                                SuggestionChip(
                                                    onClick = {
                                                        if (u.id != user?.id) {
                                                            showUserRoleDialog = u
                                                        }
                                                    },
                                                    label = { Text(u.role.name) },
                                                    modifier = Modifier.height(24.dp)
                                                )
                                            }
                                        }

                                        // Cancel/Delete Warga Action
                                        if (u.id != user?.id) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteUser(u) { success ->
                                                        if (success) {
                                                            snackbarMessage = "Akun ${u.name} berhasil dihapus."
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.testTag("delete_user_${u.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Hapus User",
                                                    tint = Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cetak Laporan (PDF/Excel) Dialog Simulator
        if (showReportExportDialog) {
            Dialog(onDismissRequest = { showReportExportDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("export_dialog")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🖨️ Cetak Laporan Real-Time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Data rekapitulasi aduan masyarakat kota Tasikmalaya berformat CSV/Excel berhasil digenerasi secara instan:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        val reportPreview = remember { viewModel.generateReportData() }

                        // Display reports database list neatly inside box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = reportPreview,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showReportExportDialog = false },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("TUTUP")
                            }

                            Button(
                                onClick = {
                                    snackbarMessage = "Laporan berhasil diekspor sebagai Laporan_Aduan_Tasikmalaya.csv!"
                                    showReportExportDialog = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("download_excel_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download Excel")
                            }
                        }
                    }
                }
            }
        }

        // Change Role dialog
        showUserRoleDialog?.let { targetUser ->
            Dialog(onDismissRequest = { showUserRoleDialog = null }) {
                Card(
                     shape = RoundedCornerShape(16.dp),
                     modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Ubah Hak Akses Pengguna",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Silakan pilih jabatan baru untuk pengguna ${targetUser.name}:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            UserRole.values().forEach { role ->
                                Button(
                                    onClick = {
                                        viewModel.changeUserRole(targetUser, role) { success ->
                                            if (success) {
                                                snackbarMessage = "Jabatan ${targetUser.name} diubah menjadi ${role.name}"
                                            }
                                            showUserRoleDialog = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (targetUser.role == role) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (targetUser.role == role) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(role.name, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        TextButton(
                            onClick = { showUserRoleDialog = null },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("BATAL")
                        }
                    }
                }
            }
        }

        // Custom snackbar message overlay
        AnimatedVisibility(
            visible = snackbarMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxWidth().background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    color = Color(0xFF1E293B),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = snackbarMessage ?: "", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(16.dp))
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
