package com.example.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complaint
import com.example.data.model.ComplaintStatus
import com.example.ui.component.CategoryIcon
import com.example.ui.component.ComplaintListItemCard
import com.example.ui.component.StatusBadge
import com.example.ui.component.NotificationsDialog
import com.example.ui.viewmodel.ComplaintViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenDashboardScreen(
    viewModel: ComplaintViewModel,
    onNavigateToAddComplaint: () -> Unit,
    onNavigateToComplaintDetail: (Complaint) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val complaints by viewModel.citizensComplaints.collectAsState()
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

    // Count statistics
    val totalAduan = complaints.size
    val prosesAduan = complaints.count { it.status == ComplaintStatus.PROSES || it.status == ComplaintStatus.DIVALIDASI }
    val selesaiAduan = complaints.count { it.status == ComplaintStatus.SELESAI }

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
                                text = "LAYANAN PENGADUAN TASIKMALAYA",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddComplaint,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("add_complaint_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Buat Laporan")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buat Aduan", fontWeight = FontWeight.Bold)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High Density Welcome banner and action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sampurasun Greeting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Sampurasun,",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF5B6060),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user?.name ?: "Warga Tasikmalaya",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Small ambient decorative element (an indicator or small outline card)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE1E3E3)),
                        color = Color.White,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF006A6A), RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WARGA AKTIF",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF006A6A),
                                fontSize = 8.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Banner: Ada Fasilitas Rusak?
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1E8E8)),
                    border = BorderStroke(1.dp, Color(0xFFAFCCCC)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ada Fasilitas Rusak?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF002020)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Laporkan kerusakan jalan, lampu, atau drainase di sekitarmu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF004F4F),
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onNavigateToAddComplaint,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006A6A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Lapor", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Statistics Grid Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "TOTAL ADUAN",
                        count = totalAduan.toString(),
                        backgroundColor = Color.White,
                        textColor = Color(0xFF191C1C),
                        borderStroke = BorderStroke(1.dp, Color(0xFFE1E3E3)),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "DIPROSES",
                        count = prosesAduan.toString(),
                        backgroundColor = Color(0xFFCCE8E8), // processes / soft teal tint matching
                        textColor = Color(0xFF006A6A),
                        borderStroke = BorderStroke(1.dp, Color(0xFF90C2C2)),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "SELESAI",
                        count = selesaiAduan.toString(),
                        backgroundColor = Color(0xFFD1FAE5), // Soft green tint
                        textColor = Color(0xFF065F46),
                        borderStroke = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Alert Banner: Total 1.240 laporan telah diselesaikan petugas minggu ini. Terima kasih kontribusinya!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFDE7D2), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFF0D5BB), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF6F4300),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Total 1.240 laporan telah diselesaikan petugas minggu ini. Terima kasih kontribusinya!",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF6F4300),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // History Label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat Pengaduan Anda",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$totalAduan Laporan",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            // Complaint list
            if (complaints.isEmpty()) {
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
                            imageVector = Icons.Default.PlaylistAddCheck,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum Ada Pengaduan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Klik tombol 'Buat Aduan' di kanan bawah untuk melaporkan kerusakan fasilitas umum kota Tasikmalaya.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(complaints) { complaint ->
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
fun StatCard(
    title: String,
    count: String,
    backgroundColor: Color,
    textColor: Color,
    borderStroke: BorderStroke? = null,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

