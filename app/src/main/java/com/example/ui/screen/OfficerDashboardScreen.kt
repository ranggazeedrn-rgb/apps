package com.example.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FilterList
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
import com.example.data.model.Complaint
import com.example.data.model.ComplaintStatus
import com.example.ui.component.ComplaintListItemCard
import com.example.ui.component.NotificationsDialog
import androidx.compose.material.icons.filled.NotificationsActive
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficerDashboardScreen(
    viewModel: ComplaintViewModel,
    onNavigateToComplaintDetail: (Complaint) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()
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

    var selectedStatusFilter by remember { mutableStateOf<ComplaintStatus?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filteredComplaints = remember(allComplaints, selectedStatusFilter) {
        if (selectedStatusFilter == null) {
            allComplaints
        } else {
            allComplaints.filter { it.status == selectedStatusFilter }
        }
    }

    // Counts
    val waitingCount = allComplaints.count { it.status == ComplaintStatus.MENUNGGU }
    val progressCount = allComplaints.count { it.status == ComplaintStatus.PROSES || it.status == ComplaintStatus.DIVALIDASI }
    val solvedCount = allComplaints.count { it.status == ComplaintStatus.SELESAI }

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
                                text = "PORTAL PETUGAS ADUAN WARGA",
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
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Welcome Section
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selamat Bekerja,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5B6060)
                    )
                    Text(
                        text = user?.name ?: "Petugas",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
 
                    // Tasks stats grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTaskCard(
                            label = "Menunggu",
                            count = waitingCount.toString(),
                            accentColor = Color(0xFF3F4948), // matching Menunggu badge
                            modifier = Modifier.weight(1f)
                        )
                        StatTaskCard(
                            label = "Diproses",
                            count = progressCount.toString(),
                            accentColor = Color(0xFF006A6A), // matching Proses badge
                            modifier = Modifier.weight(1f)
                        )
                        StatTaskCard(
                            label = "Selesai",
                            count = solvedCount.toString(),
                            accentColor = Color(0xFF065F46), // matching Selesai badge
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Filter Header and Tools
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedStatusFilter == null) "Semua Aduan Masuk" else "Aduan: ${selectedStatusFilter?.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box {
                    IconButton(
                        onClick = { showFilterMenu = true },
                        modifier = Modifier.testTag("filter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Pilih Filter Status",
                            tint = if (selectedStatusFilter != null) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Semua Status") },
                            onClick = {
                                selectedStatusFilter = null
                                showFilterMenu = false
                            }
                        )
                        ComplaintStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName) },
                                onClick = {
                                    selectedStatusFilter = status
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Complaint Listing
            if (filteredComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tidak Ada Aduan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tidak ditemukan laporan aduan masyarakat kriteria ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredComplaints) { complaint ->
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
}

@Composable
fun StatTaskCard(
    label: String,
    count: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE1E3E3)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accentColor, RoundedCornerShape(100))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF5B6060),
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
