package com.example.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complaint
import com.example.data.model.ComplaintStatus
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusBadge(status: ComplaintStatus, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (status) {
        ComplaintStatus.MENUNGGU -> Color(0xFFE1E3E3) to Color(0xFF3F4948)
        ComplaintStatus.DIVALIDASI -> Color(0xFFD1E8E8) to Color(0xFF004F4F)
        ComplaintStatus.PROSES -> Color(0xFFCCE8E8) to Color(0xFF006A6A)
        ComplaintStatus.SELESAI -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        ComplaintStatus.DITOLAK -> Color(0xFFFFDAD6) to Color(0xFF410002)
    }
    Surface(
        color = backgroundColor,
        contentColor = textColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun CategoryIcon(category: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    val icon = when (category.lowercase()) {
        "jalan rusak", "jalan raya" -> Icons.Default.Warning
        "drainase tersumbat", "saluran air", "selokan" -> Icons.Default.Build
        "lampu penerangan jalan umum mati", "lampu jalan", "penerangan" -> Icons.Default.FlashlightOn
        "sampah menumpuk", "taman & kebersihan", "sampah" -> Icons.Default.Delete
        "fasilitas taman rusak", "fasilitas sosial", "gedung", "taman" -> Icons.Default.Cabin
        else -> Icons.Default.Info
    }
    Icon(
        imageVector = icon,
        contentDescription = category,
        tint = color,
        modifier = modifier
    )
}

@Composable
fun ComplaintPhoto(photoName: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE2E8F0))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (photoName) {
                "jalan_rusak" -> {
                    // Asphalt Background
                    drawRect(color = Color(0xFF334155))
                    // Central yellow lines
                    val dashW = w / 15
                    for (i in 0..15 step 2) {
                        drawRect(
                            color = Color(0xFFF59E0B),
                            topLeft = Offset((i * dashW) + dashW / 2, h / 2 - 4f),
                            size = Size(dashW, 8f)
                        )
                    }
                    // Draws Crater / Potholes
                    val craterPath = Path().apply {
                        moveTo(w * 0.3f, h * 0.4f)
                        quadraticTo(w * 0.45f, h * 0.35f, w * 0.55f, h * 0.48f)
                        quadraticTo(w * 0.7f, h * 0.65f, w * 0.5f, h * 0.75f)
                        quadraticTo(w * 0.35f, h * 0.8f, w * 0.28f, h * 0.55f)
                        close()
                    }
                    drawPath(path = craterPath, color = Color(0xFF1E293B))
                    drawPath(path = craterPath, color = Color(0xFF0F172A), style = Stroke(width = 6f))

                    // Small cracks
                    drawLine(
                        color = Color(0xFF0F172A),
                        start = Offset(w * 0.55f, h * 0.48f),
                        end = Offset(w * 0.8f, h * 0.45f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color(0xFF0F172A),
                        start = Offset(w * 0.8f, h * 0.45f),
                        end = Offset(w * 0.9f, h * 0.55f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF0F172A),
                        start = Offset(w * 0.35f, h * 0.8f),
                        end = Offset(w * 0.2f, h * 0.88f),
                        strokeWidth = 3f
                    )
                }
                "selokan_mampet" -> {
                    // Dark swamp green Background
                    drawRect(color = Color(0xFF2C3531))
                    // Concrete bounds
                    drawRect(
                        color = Color(0xFF64748B),
                        topLeft = Offset(0f, 0f),
                        size = Size(w, h * 0.2f)
                    )
                    drawRect(
                        color = Color(0xFF64748B),
                        topLeft = Offset(0f, h * 0.8f),
                        size = Size(w, h * 0.2f)
                    )
                    // Murky water waves
                    val wavePath1 = Path().apply {
                        moveTo(0f, h * 0.3f)
                        cubicTo(w * 0.25f, h * 0.25f, w * 0.5f, h * 0.4f, w * 0.75f, h * 0.3f)
                        cubicTo(w * 0.85f, h * 0.28f, w, h * 0.35f, w, h * 0.35f)
                        lineTo(w, h * 0.75f)
                        cubicTo(w * 0.75f, h * 0.7f, w * 0.5f, h * 0.85f, w * 0.25f, h * 0.75f)
                        lineTo(0f, h * 0.75f)
                        close()
                    }
                    drawPath(path = wavePath1, color = Color(0xFF1E2824))

                    // Garbage clutter circles
                    drawCircle(color = Color(0xFFDC2626), radius = w * 0.05f, center = Offset(w * 0.4f, h * 0.5f)) // RED CAN
                    drawCircle(color = Color(0xFF2563EB), radius = w * 0.04f, center = Offset(w * 0.6f, h * 0.55f)) // BLUE PLASTIC
                    drawRoundRect(
                        color = Color(0xFFFBBF24),
                        topLeft = Offset(w * 0.2f, h * 0.42f),
                        size = Size(w * 0.12f, h * 0.08f),
                        cornerRadius = CornerRadius(10f, 10f)
                    ) // YELLOW BOX
                    // Clogging branch lines
                    drawLine(
                        color = Color(0xFF78350F),
                        start = Offset(w * 0.1f, h * 0.65f),
                        end = Offset(w * 0.5f, h * 0.45f),
                        strokeWidth = 8f
                    )
                    drawLine(
                        color = Color(0xFF78350F),
                        start = Offset(w * 0.35f, h * 0.53f),
                        end = Offset(w * 0.25f, h * 0.35f),
                        strokeWidth = 5f
                    )
                }
                "pju_mati" -> {
                    // Dark night purple gradient
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                        )
                    )
                    // Yellow stars
                    drawCircle(color = Color(0xFFFCD34D), radius = 2f, center = Offset(w * 0.2f, h * 0.2f))
                    drawCircle(color = Color(0xFFFCD34D), radius = 3f, center = Offset(w * 0.75f, h * 0.15f))
                    drawCircle(color = Color(0xFFFCD34D), radius = 2f, center = Offset(w * 0.45f, h * 0.35f))
                    drawCircle(color = Color(0xFFFCD34D), radius = 2f, center = Offset(w * 0.85f, h * 0.45f))

                    // Lamppost drawing (Metal pole)
                    val polePath = Path().apply {
                        moveTo(w * 0.25f, h)
                        lineTo(w * 0.25f, h * 0.25f)
                        quadraticTo(w * 0.25f, h * 0.15f, w * 0.45f, h * 0.15f)
                        lineTo(w * 0.55f, h * 0.15f)
                    }
                    drawPath(path = polePath, color = Color(0xFF94A3B8), style = Stroke(width = 12f, cap = StrokeCap.Round))

                    // Lamp head bowl
                    drawOval(
                        color = Color(0xFF475569),
                        topLeft = Offset(w * 0.5f, h * 0.12f),
                        size = Size(w * 0.15f, h * 0.08f)
                    )
                    // Dead Bulb
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = w * 0.04f,
                        center = Offset(w * 0.575f, h * 0.22f)
                    )
                    drawCircle(
                        color = Color(0xFF64748B),
                        radius = w * 0.04f,
                        center = Offset(w * 0.575f, h * 0.22f),
                        style = Stroke(width = 3f)
                    )
                    // "X" cross out indicating broken bulb
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(w * 0.54f, h * 0.19f),
                        end = Offset(w * 0.61f, h * 0.25f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(w * 0.61f, h * 0.19f),
                        end = Offset(w * 0.54f, h * 0.25f),
                        strokeWidth = 4f
                    )
                }
                "tumpukan_sampah" -> {
                    // Soft light green background representing green space
                    drawRect(color = Color(0xFFE2E8F0))
                    // Sidewalk curb
                    drawRect(
                        color = Color(0xFF94A3B8),
                        topLeft = Offset(0f, h * 0.75f),
                        size = Size(w, h * 0.25f)
                    )
                    // Trash bin body
                    drawRoundRect(
                        color = Color(0xFF15803D), // Green bin
                        topLeft = Offset(w * 0.35f, h * 0.45f),
                        size = Size(w * 0.3f, h * 0.35f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    // Recycler logo outline
                    drawCircle(
                        color = Color(0xFFFFFFFF),
                        radius = w * 0.04f,
                        center = Offset(w * 0.5f, h * 0.62f),
                        style = Stroke(width = 3f)
                    )

                    // Overflown rubbish shapes popping out
                    drawCircle(color = Color(0xFFD97706), radius = w * 0.05f, center = Offset(w * 0.42f, h * 0.41f))
                    drawCircle(color = Color(0xFF2563EB), radius = w * 0.04f, center = Offset(w * 0.52f, h * 0.38f))
                    drawRoundRect(
                        color = Color(0xFFDC2626),
                        topLeft = Offset(w * 0.32f, h * 0.42f),
                        size = Size(w * 0.08f, h * 0.08f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    // Trash heaps scattered on ground
                    drawCircle(color = Color(0xFF4B5563), radius = 12f, center = Offset(w * 0.28f, h * 0.77f))
                    drawCircle(color = Color(0xFF78350F), radius = 18f, center = Offset(w * 0.72f, h * 0.8f))
                    drawCircle(color = Color(0xFF475569), radius = 10f, center = Offset(w * 0.75f, h * 0.75f))
                }
                "taman_rusak" -> {
                    // Sky and lawn background
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFBAE6FD), Color(0xFFBBF7D0))
                        )
                    )
                    // Sun
                    drawCircle(color = Color(0xFFFDE047), radius = w * 0.08f, center = Offset(w * 0.85f, h * 0.25f))

                    // Broken bench seat plank 1
                    drawRoundRect(
                        color = Color(0xFFB45309), // Brown plank
                        topLeft = Offset(w * 0.2f, h * 0.55f),
                        size = Size(w * 0.55f, h * 0.05f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    // Broken bench seat plank 2 angled (looks cracked)
                    val brokenPlankPath = Path().apply {
                        moveTo(w * 0.2f, h * 0.61f)
                        lineTo(w * 0.45f, h * 0.61f)
                        lineTo(w * 0.62f, h * 0.72f) // bent down
                        lineTo(w * 0.61f, h * 0.75f)
                        lineTo(w * 0.43f, h * 0.64f)
                        lineTo(w * 0.2f, h * 0.64f)
                        close()
                    }
                    drawPath(path = brokenPlankPath, color = Color(0xFF92400E))

                    // Bench legs (steel structures, one tipped over or cracked)
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(w * 0.25f, h * 0.55f),
                        end = Offset(w * 0.22f, h * 0.85f),
                        strokeWidth = 8f
                    )
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(w * 0.7f, h * 0.55f),
                        end = Offset(w * 0.78f, h * 0.72f), // Short/broken leg!
                        strokeWidth = 8f
                    )
                }
                else -> {
                    // Generic Civic Emblem Shield
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF0F766E), Color(0xFF042F2E))
                        )
                    )
                    // Inner glowing shield outline
                    val shieldPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.25f)
                        quadraticTo(w * 0.75f, h * 0.25f, w * 0.75f, h * 0.5f)
                        quadraticTo(w * 0.75f, h * 0.75f, w * 0.5f, h * 0.85f)
                        quadraticTo(w * 0.25f, h * 0.75f, w * 0.25f, h * 0.5f)
                        quadraticTo(w * 0.25f, h * 0.25f, w * 0.5f, h * 0.25f)
                        close()
                    }
                    drawPath(path = shieldPath, color = Color(0xFFFBBF24).copy(alpha = 0.3f))
                    drawPath(path = shieldPath, color = Color(0xFFFBBF24), style = Stroke(width = 4f))

                    // Golden tool symbols in center
                    drawLine(
                        color = Color(0xFFFFFFFF),
                        start = Offset(w * 0.4f, h * 0.6f),
                        end = Offset(w * 0.6f, h * 0.4f),
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = Color(0xFFFFFFFF),
                        start = Offset(w * 0.42f, h * 0.42f),
                        end = Offset(w * 0.58f, h * 0.58f),
                        strokeWidth = 6f
                    )
                }
            }
        }
    }
}

@Composable
fun ComplaintListItemCard(
    complaint: Complaint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        .format(Date(complaint.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE1E3E3)),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("complaint_item_${complaint.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon with High Density Background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (MaterialTheme.colorScheme.surface == Color.White || MaterialTheme.colorScheme.surface == Color(0xFFFFFFFF)) Color(0xFFF0F5F5) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CategoryIcon(category = complaint.category, modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = complaint.category,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5B6060)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = complaint.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF5B6060),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = complaint.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5B6060),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = complaint.status)
                    
                    // Small icon indicator if responding
                    if (complaint.feedback != null) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Ada tanggapan",
                            tint = Color(0xFF006A6A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsDialog(
    notifications: List<com.example.data.model.Notification>,
    onDismissRequest: () -> Unit,
    onMarkAllRead: () -> Unit,
    onMarkRead: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFE1E3E3)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notifikasi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${notifications.count { !it.isRead }} Belum dibaca",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5B6060)
                        )
                    }
                    if (notifications.isNotEmpty()) {
                        TextButton(
                            onClick = onClearAll,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Hapus Semua", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFBA1A1A)))
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE1E3E3))
                )

                if (notifications.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Belum Ada Notifikasi",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF5B6060)
                        )
                        Text(
                            text = "Semua status perubahan aduan akan tampil di sini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            val readableTime = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(notif.timestamp))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (notif.isRead) Color.Transparent else Color(0xFFD1E8E8).copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (notif.isRead) Color(0xFFE1E3E3) else Color(0xFFAFCCCC),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (!notif.isRead) {
                                            onMarkRead(notif.id)
                                        }
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Status Indicator
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .offset(y = 4.dp)
                                        .background(
                                            if (notif.isRead) Color.Gray.copy(alpha = 0.5f) else Color(0xFF006A6A),
                                            RoundedCornerShape(50)
                                        )
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = readableTime,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF3F4948),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE1E3E3))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (notifications.any { !it.isRead }) {
                        Button(
                            onClick = onMarkAllRead,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006A6A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Baca Semua", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    OutlinedButton(
                        onClick = onDismissRequest,
                        border = BorderStroke(1.dp, Color(0xFF006A6A)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF006A6A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = if (notifications.any { !it.isRead }) Modifier.weight(1f) else Modifier.fillMaxWidth()
                    ) {
                        Text("Tutup", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
