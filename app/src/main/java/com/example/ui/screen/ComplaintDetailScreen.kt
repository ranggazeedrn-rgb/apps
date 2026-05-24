package com.example.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import com.example.data.model.UserRole
import com.example.ui.component.CategoryIcon
import com.example.ui.component.ComplaintPhoto
import com.example.ui.component.StatusBadge
import com.example.ui.viewmodel.ComplaintViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    viewModel: ComplaintViewModel,
    complaintId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()

    // Find the latest complaint state from Flow
    val complaint = remember(allComplaints, complaintId) {
        allComplaints.find { it.id == complaintId }
    }

    var feedbackInput by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(complaint?.status ?: ComplaintStatus.MENUNGGU) }
    var isUpdating by remember { mutableStateOf(false) }

    // Synchronize selectedStatus when complaint updates
    LaunchedEffect(complaint) {
        complaint?.let {
            selectedStatus = it.status
            feedbackInput = "" // Clear after update
        }
    }

    val scrollState = rememberScrollState()

    if (complaint == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail Aduan") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Laporan tidak ditemukan.")
            }
        }
        return
    }

    val dateStr = SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale("id", "ID"))
        .format(Date(complaint.timestamp))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lacak Status Aduan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulated Photo Box
            ComplaintPhoto(
                photoName = complaint.photoName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("complaint_photo")
            )

            // Header info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIcon(category = complaint.category, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = complaint.category,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusBadge(status = complaint.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = complaint.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = complaint.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pelapor: ${complaint.authorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Dilaporkan: $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }

            // Report Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Deskripsi Laporan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = complaint.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Real-time status tracking timeline UI
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pelacakan Status Real-Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val trackingStages = listOf(
                        Triple("Lumpuh Terkirim (Menunggu)", "Aduan Anda berhasil terdaftar di sistem kami.", complaint.status >= ComplaintStatus.MENUNGGU),
                        Triple("Laporan Divalidasi", "Petugas telah melakukan peninjauan administratif.", complaint.status >= ComplaintStatus.DIVALIDASI),
                        Triple("Sedang Diperbaiki (Proses)", "Aduan dialokasikan ke dinas terkait untuk perbaikan.", complaint.status >= ComplaintStatus.PROSES),
                        Triple("Selesai Ditangani", "Perbaikan fasilitas umum telah rampung di lapangan.", complaint.status == ComplaintStatus.SELESAI)
                    )

                    Column {
                        trackingStages.forEachIndexed { idx, (stageName, stageDesc, isActive) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    if (idx < trackingStages.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(40.dp)
                                                .background(if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = stageName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
                                    )
                                    Text(
                                        text = stageDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isActive) Color.DarkGray else Color.LightGray
                                    )
                                }
                            }
                        }
                    }

                    // Special Rejected layout if status is DITOLAK
                    if (complaint.status == ComplaintStatus.DITOLAK) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🔴 LAPORAN DITOLAK: Aduan ini dinilai tidak memenuhi kriteria, duplikat, atau berada di luar yurisdiksi Kota Tasikmalaya.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    }
                }
            }

            // Feedback Tanggapan Petugas Section if exists
            complaint.feedback?.let { fb ->
                val fbDate = if (complaint.feedbackTimestamp != null) {
                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                        .format(Date(complaint.feedbackTimestamp))
                } else ""
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("feedback_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Message, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tanggapan Instansi Resmi",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                            }
                            Text(text = fbDate, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = fb,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 22.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Menjawab: ${complaint.feedbackAuthor ?: "Petugas PUPR/DLH"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            // OFFICER & ADMIN: Validation & Status update Panel
            if (user != null && (user!!.role == UserRole.PETUGAS || user!!.role == UserRole.ADMIN)) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth().testTag("officer_response_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🛠️ Panel Tindak Lanjut Petugas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Selector Status
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Tentukan Status Baru Laporan:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            
                            // Visual horizontal selection chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ComplaintStatus.values().forEach { status ->
                                    ElevatedFilterChip(
                                        selected = selectedStatus == status,
                                        onClick = { selectedStatus = status },
                                        label = { Text(status.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.elevatedFilterChipColors(
                                            selectedContainerColor = status.getStatusColor(),
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f).testTag("status_chip_${status.name}")
                                    )
                                }
                            }
                        }

                        // Text Area input for feedback
                        OutlinedTextField(
                            value = feedbackInput,
                            onValueChange = { feedbackInput = it },
                            label = { Text("Ketik Tanggapan / Progress Lapangan") },
                            placeholder = { Text("Contoh: Perbaikan aspal berlubang dijadwalkan tanggal...") },
                            trailingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth().testTag("feedback_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Submit Button
                        Button(
                            onClick = {
                                isUpdating = true
                                // Write to db
                                viewModel.updateComplaintStatus(
                                    complaintId = complaint.id,
                                    newStatus = selectedStatus,
                                    feedbackText = feedbackInput.trim()
                                ) { success ->
                                    isUpdating = false
                                }
                            },
                            enabled = !isUpdating,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("submit_response_button")
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("SIMPAN TINDAK LANJUT", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ADMIN ONLY: Delete Complaint Button
            if (user != null && user!!.role == UserRole.ADMIN) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteComplaint(complaint.id) { success ->
                            if (success) {
                                onNavigateBack()
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("admin_delete_complaint_btn")
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("HAPUS LAPORAN INI SECARA PERMANEN", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
