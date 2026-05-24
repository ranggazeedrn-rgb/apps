package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.component.ComplaintPhoto
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddComplaintScreen(
    viewModel: ComplaintViewModel,
    onNavigateBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Jalan Rusak") }
    var location by remember { mutableStateOf("Kecamatan Cipedes") }
    var photoName by remember { mutableStateOf("jalan_rusak") } // default starting simulated photo
    var hasPhotoSelected by remember { mutableStateOf(false) }

    var showCameraSimDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessSnack by remember { mutableStateOf(false) }

    val categories = listOf(
        "Jalan Rusak",
        "Lampu Penerangan Jalan Umum Mati",
        "Sampah Menumpuk",
        "Drainase Tersumbat",
        "Fasilitas Taman Rusak",
        "Lainnya"
    )
    val subdistricts = listOf(
        "Kecamatan Cipedes",
        "Kecamatan Indihiang",
        "Kecamatan Cihideung",
        "Kecamatan Tawang",
        "Kecamatan Kawalu",
        "Kecamatan Mangkubumi",
        "Kecamatan Tamansari"
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Aduan Baru", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Description Banner Banner
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "Laporan Resmi Fasilitas Umum",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Pastikan data, lokasi, dan foto bukti yang dikirimkan jelas serta akurat agar petugas dapat segera melakukan verifikasi lapangan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Input Title
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Judul Laporan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Contoh: Lampu Jalan Padam di Sukaratu") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complaint_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Category Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Kategori Kerusakan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                // Horizontally scrollable row of chips (simplified list)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Display Category Selection as FlowRow or nicely styled custom column/chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.take(3).forEach { cat ->
                                    val isSelected = category == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { category = cat },
                                        label = { Text(cat) },
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.testTag("category_chip_$cat")
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.drop(3).forEach { cat ->
                                    val isSelected = category == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { category = cat },
                                        label = { Text(cat) },
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.testTag("category_chip_$cat")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Location/District Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Wilayah Kecamatan Tasikmalaya",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Location dropdown container
                var expandedSubdistrict by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedSubdistrict = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Pilih Kecamatan"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSubdistrict = true }
                            .testTag("location_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = expandedSubdistrict,
                        onDismissRequest = { expandedSubdistrict = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        subdistricts.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub) },
                                onClick = {
                                    location = sub
                                    expandedSubdistrict = false
                                }
                            )
                        }
                    }
                }
            }

            // Detailed Description
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Rincian & Kronologi Kerusakan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Tuliskan deskripsi lengkap, misalnya dampak dari kerusakan, rincian lokasi persis, tinggi/lebar lubang, atau waktu padam.") },
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complaint_desc_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Photo Upload
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Foto Bukti Kerusakan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (hasPhotoSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        ComplaintPhoto(
                            photoName = photoName,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Snap again button
                        Button(
                            onClick = { showCameraSimDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)),
                            modifier = Modifier
                                .padding(12.dp)
                                .testTag("change_photo_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ganti Foto", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable { showCameraSimDialog = true }
                            .testTag("camera_placeholder_card")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Lampirkan Foto Kerusakan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ketuk untuk membuka Simulasi Kamera",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action submit
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        isSubmitting = true
                        // Submit to Room DB via VM
                        viewModel.submitComplaint(
                            title = title,
                            description = description,
                            category = category,
                            location = location,
                            photoName = if (hasPhotoSelected) photoName else "default"
                        ) { success ->
                            isSubmitting = false
                            if (success) {
                                showSuccessSnack = true
                                onSubmitSuccess()
                            }
                        }
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank() && !isSubmitting,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_complaint_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("KIRIM LAPORAN PENGADUAN", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Camera Simulated Dialog
        if (showCameraSimDialog) {
            Dialog(onDismissRequest = { showCameraSimDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "📸 Simulasi Kamera Pintar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Pilih tipe visual kerusakan untuk mensimulasikan foto jepretan kamera handphone Anda:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        val photoPresets = listOf(
                            "jalan_rusak" to "Asphalt Retak & Berlubang",
                            "selokan_mampet" to "Saluran Air Tersumbat Sampah",
                            "pju_mati" to "Tiang Lampu Jalan Padam Gelap",
                            "tumpukan_sampah" to "Tempat Sampah Meluber",
                            "taman_rusak" to "Fasilitas Kursi Taman Patah"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            photoPresets.forEach { (preset, label) ->
                                Button(
                                    onClick = {
                                        photoName = preset
                                        hasPhotoSelected = true
                                        showCameraSimDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (photoName == preset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (photoName == preset) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("camera_preset_$preset")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (photoName == preset) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        TextButton(
                            onClick = { showCameraSimDialog = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("BATAL", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
