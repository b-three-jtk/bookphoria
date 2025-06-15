package com.example.bookphoria.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookphoria.data.local.dao.ReadingSummary
import com.example.bookphoria.ui.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Color palette
private val primaryPeach = Color(0xFFF8B8AF)
private val primaryGreen = Color(0xFFE75234)
private val backgroundBeige = Color(0xFFF2EFEA)
private val textDark = Color(0xFF2D2D2D)
private val textGray = Color(0xFF666666)
private val textLight = Color(0xFF999999)

@Composable
fun RecapScreen(viewModel: HomeViewModel, navController: NavController) {
    val readingSummary by viewModel.readingSummary.collectAsState()

    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val startDate = today.minusDays(6).toString()
        val endDate = today.toString()

        viewModel.loadReadingSummary(startDate, endDate)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBeige)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar with Back Button
            TopBar(
                onBackClick = { navController.popBackStack() }
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (readingSummary.isNotEmpty()) {
                    // Header Stats
                    ReadingStatsHeader(summary = readingSummary)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Enhanced Bar Chart
                    EnhancedBarChartCard(summary = readingSummary)
                } else {
                    Spacer(modifier = Modifier.height(40.dp))
                    EmptyStateCard()
                }
            }
        }
    }
}

@Composable
fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(44.dp)
                .background(
                    Color.White.copy(alpha = 0.9f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = textDark,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Rekap Bacaan",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = textDark
        )
    }
}

@Composable
fun ReadingStatsHeader(summary: List<ReadingSummary>) {
    val totalPages = summary.sumOf { it.totalPages }
    val averagePages = if (summary.isNotEmpty()) totalPages / summary.size else 0
    val bestDay = summary.maxByOrNull { it.totalPages }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            primaryPeach.copy(alpha = 0.8f),
                            primaryGreen.copy(alpha = 0.8f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Statistik Minggu Ini",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "Total Halaman",
                        value = totalPages.toString(),
                        icon = Icons.Default.Book,
                        color = Color.White
                    )

                    StatItem(
                        title = "Rata-rata/Hari",
                        value = "$averagePages",
                        subtitle = "halaman",
                        icon = Icons.Default.TrendingUp,
                        color = Color.White
                    )

                    if (bestDay != null) {
                        StatItem(
                            title = "Hari Terbaik",
                            value = "${bestDay.totalPages}",
                            subtitle = formatDate(bestDay.date),
                            icon = Icons.Default.AccessTime,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.25f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = color,
            textAlign = TextAlign.Center
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp
            ),
            color = color.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp
                ),
                color = color.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedBarChartCard(summary: List<ReadingSummary>) {
    val maxPages = summary.maxOfOrNull { it.totalPages }?.coerceAtLeast(1) ?: 1
    val barWidth = 36.dp // Ukuran bar yang lebih kecil dan konsisten
    val maxBarHeight = 140.dp
    val yAxisSteps = 5
    val stepValue = (maxPages.toFloat() / (yAxisSteps - 1)).let { if (it < 1) 1f else it }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // Title with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                primaryPeach.copy(alpha = 0.15f),
                                primaryGreen.copy(alpha = 0.15f)
                            )
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                primaryPeach.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = primaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Progress 7 Hari Terakhir",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = textDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Y-axis labels
                Column(
                    modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    (0 until yAxisSteps).reversed().forEach { step ->
                        val value = (stepValue * step).toInt()
                        Text(
                            text = "$value",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = textGray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                // Chart area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 12.dp, end = 8.dp) // Tambah padding kanan
                ) {
                    // Grid lines
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawGridLines(yAxisSteps)
                    }

                    // Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        summary.forEach { item ->
                            val barHeight = if (maxPages > 0) {
                                (item.totalPages.toFloat() / maxPages) * maxBarHeight.value
                            } else 0f

                            EnhancedBarItem(
                                height = barHeight.dp,
                                width = barWidth,
                                pages = item.totalPages,
                                date = item.date,
                                isHighest = item.totalPages == maxPages,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedBarItem(
    height: androidx.compose.ui.unit.Dp,
    width: androidx.compose.ui.unit.Dp,
    pages: Int,
    date: String,
    isHighest: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        // Pages indicator above bar
        if (pages > 0) {
            Box(
                modifier = Modifier
                    .background(
                        if (isHighest) primaryGreen.copy(alpha = 0.15f) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$pages",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = if (isHighest) primaryGreen else textGray
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Bar with gradient
        Box(
            modifier = Modifier
                .height(height.coerceAtLeast(6.dp))
                .width(36.dp) // Fixed width yang lebih kecil
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (pages > 0) {
                        if (isHighest) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    primaryPeach,
                                    primaryGreen
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    primaryPeach.copy(alpha = 0.7f),
                                    primaryGreen.copy(alpha = 0.7f)
                                )
                            )
                        }
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE8E8E8),
                                Color(0xFFD0D0D0)
                            )
                        )
                    }
                )
        ) {
            // Shine effect for highest bar
            if (isHighest && pages > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Date label
        Text(
            text = formatDateLabel(date),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isHighest) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isHighest) primaryGreen else textLight,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryPeach.copy(alpha = 0.2f),
                                primaryGreen.copy(alpha = 0.1f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Belum Ada Data Bacaan",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mulai membaca buku untuk melihat\nprogress bacaan mingguan Anda",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                color = textGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// Helper functions
private fun DrawScope.drawGridLines(steps: Int) {
    val stepHeight = size.height / (steps - 1)
    (1 until steps - 1).forEach { i ->
        drawLine(
            color = Color(0xFFF5F5F5),
            start = Offset(0f, i * stepHeight),
            end = Offset(size.width, i * stepHeight),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault()))
    } catch (e: Exception) {
        dateString.takeLast(2)
    }
}

private fun formatDateLabel(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()

        when {
            date == today -> "Hari ini"
            date == today.minusDays(1) -> "Kemarin"
            else -> {
                val dayName = date.format(DateTimeFormatter.ofPattern("E", Locale.getDefault()))
                val dayNumber = date.format(DateTimeFormatter.ofPattern("dd"))
                "$dayName\n$dayNumber"
            }
        }
    } catch (e: Exception) {
        dateString.takeLast(2)
    }
}