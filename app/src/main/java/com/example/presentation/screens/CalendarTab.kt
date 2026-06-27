package com.example.presentation.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BrandProfile
import com.example.data.PosterCatalog
import com.example.data.PosterTemplate
import com.example.presentation.PosterGenerator
import com.example.presentation.PosterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTab(
    viewModel: PosterViewModel,
    onPosterSelected: (PosterTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val postersList by viewModel.postersList.collectAsState()
    val apiLogs by viewModel.apiLogs.collectAsState()

    // Pickers visibility
    var showYearDropdown by remember { mutableStateOf(false) }
    var showMonthDropdown by remember { mutableStateOf(false) }

    // Dialog state for "Date Details"
    var activeDetailsDay by remember { mutableStateOf<Int?>(null) }
    var isApiFetching by remember { mutableStateOf(false) }
    var selectedDetailsDateStr by remember { mutableStateOf("") }
    var secureConnectionStatus by remember { mutableStateOf("") }

    val categories = listOf("All", "Festival", "Business", "Marketing", "National Day", "Birthday", "Offer")
    val yearsList = listOf(2024, 2025, 2026, 2027)
    val monthsList = DateFormatSymbols().months.filter { it.isNotEmpty() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- 1. Smart Feature Intro Banner ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Wifi, contentDescription = "Secure API", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Secure API Sync Active",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32)),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.CloudQueue, contentDescription = "Cache Status", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Cache: 42.6 MB",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary),
                fontSize = 11.sp
            )
        }

        // --- 2. Beautiful Year / Month Picker Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Year & Month Selector Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.prevMonth() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Prev Month",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Year dropdown trigger button
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Button(
                                onClick = { showYearDropdown = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text(
                                    text = "$currentYear",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            }

                            DropdownMenu(
                                expanded = showYearDropdown,
                                onDismissRequest = { showYearDropdown = false }
                            ) {
                                yearsList.forEach { yr ->
                                    DropdownMenuItem(
                                        text = { Text("$yr", fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            viewModel.selectYear(yr)
                                            showYearDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Month dropdown trigger button
                        Box {
                            val activeMonthName = monthsList[currentMonth]
                            Button(
                                onClick = { showMonthDropdown = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text(
                                    text = activeMonthName,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            }

                            DropdownMenu(
                                expanded = showMonthDropdown,
                                onDismissRequest = { showMonthDropdown = false }
                            ) {
                                monthsList.forEachIndexed { idx, name ->
                                    DropdownMenuItem(
                                        text = { Text(name, fontWeight = FontWeight.SemiBold) },
                                        onClick = {
                                            viewModel.selectMonth(idx)
                                            showMonthDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days of the Week headers
                val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Calendar Grid calculation
                val calendarData = remember(currentYear, currentMonth) {
                    getDaysForMonth(currentYear, currentMonth)
                }

                val rows = calendarData.chunked(7)
                rows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        row.forEach { dayValue ->
                            val isSelected = dayValue > 0 && isDateSelected(currentYear, currentMonth, dayValue, selectedDate)
                            val isToday = dayValue > 0 && isTodayDate(currentYear, currentMonth, dayValue)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.5f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.secondary
                                            isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable(enabled = dayValue > 0) {
                                        coroutineScope.launch {
                                            // Format selected date string
                                            val cal = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, currentYear)
                                                set(Calendar.MONTH, currentMonth)
                                                set(Calendar.DAY_OF_MONTH, dayValue)
                                            }
                                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                            selectedDetailsDateStr = sdf.format(cal.time)

                                            // Simulate cloud database retrieval flow
                                            isApiFetching = true
                                            secureConnectionStatus = "Initializing Secure Handshake..."
                                            delay(150)
                                            secureConnectionStatus = "Calling /api/posters?date=$selectedDetailsDateStr..."
                                            delay(200)
                                            secureConnectionStatus = "Resolving HD Content Delivery Network URLs..."
                                            delay(150)
                                            isApiFetching = false

                                            // Open details Dialog
                                            activeDetailsDay = dayValue
                                            viewModel.selectDate(currentYear, currentMonth, dayValue)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayValue > 0) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dayValue.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.onSecondary
                                                isToday -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.onBackground
                                            }
                                        )
                                        // Simple tiny dot indicator for days containing major predefined festival
                                        val dateStrToCheck = remember(currentYear, currentMonth, dayValue) {
                                            val cal = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, currentYear)
                                                set(Calendar.MONTH, currentMonth)
                                                set(Calendar.DAY_OF_MONTH, dayValue)
                                            }
                                            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                                        }
                                        val hasFestival = remember(dateStrToCheck) {
                                            PosterCatalog.templates.any { it.dateStr == dateStrToCheck && it.category == "Festival" }
                                        }
                                        if (hasFestival) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) MaterialTheme.colorScheme.onSecondary else Color(0xFFFF9100))
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

        // --- 3. Category Chip Filter Scroll ---
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCategory(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        selected = isSelected,
                        enabled = true,
                        selectedBorderColor = Color.Transparent,
                        borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                )
            }
        }

        // --- 4. Today's Feed Overview ---
        val dateLabel = formatFriendlyDate(selectedDate)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Posters for $dateLabel",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "${postersList.size} designs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- 5. Regular Feed Grid ---
        if (postersList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No posters available for this criteria",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // restricted height because outer Column is scrollable
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false // let outer vertical scroll dominate
            ) {
                items(postersList.take(4)) { template ->
                    PosterCard(
                        template = template,
                        onClick = { onPosterSelected(template) }
                    )
                }
            }
        }

        // --- 6. Live API Console Terminal Block ---
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "📡 Live API Terminal Monitor",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Text(
            text = "Observe live secure JSON REST API logs as you perform year, month and day clicks in the smart calendar.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "api.poster365.com - STABLE",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                        )
                    }
                    Text(
                        text = "HTTPS/TLS 1.3",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                    )
                }

                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Black)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (apiLogs.isEmpty()) {
                        Text(
                            text = "Terminal idle. Clicks will trigger server requests...",
                            color = Color.LightGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    } else {
                        Column {
                            apiLogs.forEach { log ->
                                Text(
                                    text = "[${log.timestamp}] -> ${log.method} ${log.endpoint} ${log.status}",
                                    color = if (log.status.contains("200")) Color(0xFF00E676) else Color(0xFFFF1744),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = log.payload,
                                    color = Color(0xFF00E5FF),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // --- Simulated Secure API Load Overlay ---
    if (isApiFetching) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Secure API Call",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = secureConnectionStatus,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // --- Interactive Date Details Sheet Dialog ---
    if (activeDetailsDay != null) {
        val dayVal = activeDetailsDay!!
        val dateStr = selectedDetailsDateStr
        val datePosters = remember(dateStr) { PosterCatalog.getPostersForDate(dateStr) }
        val eventName = remember(datePosters, dateStr) {
            datePosters.firstOrNull { it.dateStr == dateStr }?.title ?: "Special Creative Day"
        }
        val eventDesc = remember(datePosters) {
            datePosters.firstOrNull()?.description ?: "Celebrate today with premium styled designs automatically branded for your business!"
        }

        Dialog(onDismissRequest = { activeDetailsDay = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Date Details API",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "GET /api/posters?date=$dateStr",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                                color = Color(0xFF00E5FF)
                            )
                        }
                        IconButton(onClick = { activeDetailsDay = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close details")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Festival Info Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Festival, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Festival: $eventName",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = eventDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Available Posters (${datePosters.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Horizontal list of posters in details sheet
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                    ) {
                        items(datePosters) { template ->
                            val resId = remember(template.drawableResName) {
                                context.resources.getIdentifier(template.drawableResName, "drawable", context.packageName)
                            }
                            val brandProfileState by viewModel.brandProfile.collectAsState()
                            val profile = brandProfileState ?: BrandProfile()

                            Card(
                                modifier = Modifier
                                    .width(180.dp)
                                    .wrapContentHeight(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .background(Color.LightGray)
                                    ) {
                                        if (resId != 0) {
                                            androidx.compose.foundation.Image(
                                                painter = painterResource(id = resId),
                                                contentDescription = template.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        // Badge
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .background(Color(0xFF00E5FF), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "HD",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = template.title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Direct download & share buttons in dialog
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // Customize (Edit) Action
                                            IconButton(
                                                onClick = {
                                                    activeDetailsDay = null
                                                    onPosterSelected(template)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(32.dp)
                                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                                            }

                                            // Quick Download Action
                                            IconButton(
                                                onClick = {
                                                    val slogan = template.slogans.firstOrNull()?.text ?: "Branded with Poster365"
                                                    val bitmap = PosterGenerator.generatePosterBitmap(
                                                        context = context,
                                                        template = template,
                                                        profile = profile,
                                                        sloganText = slogan,
                                                        showBrandName = true,
                                                        showMobile = true,
                                                        showAddress = true,
                                                        showSocial = true,
                                                        showLogo = true,
                                                        showQr = true,
                                                        overlayColorHex = profile.primaryColorHex
                                                    )
                                                    savePosterToGalleryInTab(context, bitmap, template.title)
                                                    viewModel.recordDownload(template, slogan)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(32.dp)
                                                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(14.dp))
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
    }
}

// Inline helper for Calendar Tab Quick Download Gallery saving
private fun savePosterToGalleryInTab(context: Context, bitmap: Bitmap, title: String) {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "Poster365-${title.replace(" ", "_")}-${System.currentTimeMillis()}.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Poster365")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    try {
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            val stream = resolver.openOutputStream(imageUri)
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                Toast.makeText(context, "HD Poster saved directly from API to Gallery!", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun PosterCard(
    template: PosterTemplate,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(template.drawableResName) {
        context.resources.getIdentifier(template.drawableResName, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("poster_card_${template.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(Color.DarkGray)
            ) {
                if (resId != 0) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = resId),
                        contentDescription = template.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Base Image", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                // Category badge overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = template.category,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
            
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = template.dateLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

// Helpers for Calendar Calculation
private fun getDaysForMonth(year: Int, month: Int): List<Int> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday ... 7 = Saturday
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val list = mutableListOf<Int>()
    for (i in 1 until firstDayOfWeek) {
        list.add(0)
    }
    for (i in 1..daysInMonth) {
        list.add(i)
    }
    return list
}

private fun isDateSelected(year: Int, month: Int, day: Int, selectedDateStr: String): Boolean {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val curStr = sdf.format(cal.time)
    return curStr == selectedDateStr
}

private fun isTodayDate(year: Int, month: Int, day: Int): Boolean {
    val today = Calendar.getInstance()
    return today.get(Calendar.YEAR) == year &&
            today.get(Calendar.MONTH) == month &&
            today.get(Calendar.DAY_OF_MONTH) == day
}

private fun formatFriendlyDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(dateStr) ?: return dateStr
        val friendlyFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        friendlyFormat.format(date)
    } catch (e: Exception) {
        dateStr
    }
}
