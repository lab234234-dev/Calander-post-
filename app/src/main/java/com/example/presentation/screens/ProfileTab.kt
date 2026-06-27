package com.example.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BrandProfile
import com.example.presentation.DownloadedPoster
import com.example.presentation.PosterViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    viewModel: PosterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val brandProfile by viewModel.brandProfile.collectAsState()
    val downloadedHistory by viewModel.downloadedPosters.collectAsState()
    var activeSubTab by remember { mutableIntStateOf(0) } // 0 = Profile Setup, 1 = My Downloads

    val profile = brandProfile ?: BrandProfile()

    // Form inputs state
    var brandName by remember(profile) { mutableStateOf(profile.brandName) }
    var shopName by remember(profile) { mutableStateOf(profile.shopName) }
    var mobileNumber by remember(profile) { mutableStateOf(profile.mobileNumber) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var address by remember(profile) { mutableStateOf(profile.address) }
    var socialHandle by remember(profile) { mutableStateOf(profile.socialHandle) }
    var website by remember(profile) { mutableStateOf(profile.website) }
    var qrText by remember(profile) { mutableStateOf(profile.qrText) }
    var tagline by remember(profile) { mutableStateOf(profile.tagline) }
    var logoType by remember(profile) { mutableStateOf(profile.logoType) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // High-fidelity tab switcher for Profile Screen
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Brand Setup", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.BusinessCenter, contentDescription = null) },
                modifier = Modifier.testTag("subtab_brand_setup")
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("My Studio (${downloadedHistory.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                modifier = Modifier.testTag("subtab_my_studio")
            )
        }

        if (activeSubTab == 0) {
            // BRAND SETUP SCROLLABLE VIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Hero visual banner introducing business profile
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Business Brand Setup",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Setup your brand card once and let Poster365 automatically generate, layout, and position your professional advertisement strip onto all daily posters!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- SECTION 1: CORE BRAND IDENTIFIERS ---
                Text(
                    "Primary Information",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Brand / Business Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("input_brand_name"),
                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Category / Subtitle") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("input_shop_name"),
                    placeholder = { Text("e.g. Organic Fruits, Mobile Store") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Brand Tagline / Slogan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    placeholder = { Text("e.g. Quality and Trust Since 1999") },
                    leadingIcon = { Icon(Icons.Default.RateReview, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                // --- SECTION 2: LOGO BADGE STYLE SELECTOR ---
                Text(
                    "Select Logo Shape Badge",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                val logoOptions = listOf(
                    "geometric_circle" to "Circle Badge",
                    "geometric_square" to "Square Badge",
                    "shield" to "Royal Shield",
                    "flower" to "Flower Star"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    logoOptions.forEach { (optionType, label) ->
                        val isSelected = logoType == optionType
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .clickable { logoType = optionType },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val icon = when (optionType) {
                                    "geometric_circle" -> Icons.Default.Circle
                                    "geometric_square" -> Icons.Default.Square
                                    "shield" -> Icons.Default.Security
                                    else -> Icons.Default.FilterVintage
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // --- SECTION 3: CONTACTS & CHANNELS ---
                Text(
                    "Contacts & Social Media",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Mobile Number (with WhatsApp)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("input_mobile"),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = socialHandle,
                    onValueChange = { socialHandle = it },
                    label = { Text("Social Media Handle (Instagram/X)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    placeholder = { Text("e.g. @parth_textiles") },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website Link") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    placeholder = { Text("e.g. www.parthtextiles.com") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = qrText,
                    onValueChange = { qrText = it },
                    label = { Text("QR Code Scanner URL/Payload") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    placeholder = { Text("Scan QR triggers this link...") },
                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Shop/Office Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary),
                    maxLines = 2
                )

                // --- ACTION SAVE BUTTON ---
                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        val updatedProfile = profile.copy(
                            brandName = brandName,
                            shopName = shopName,
                            mobileNumber = mobileNumber,
                            email = email,
                            address = address,
                            socialHandle = socialHandle,
                            website = website,
                            qrText = qrText,
                            tagline = tagline,
                            logoType = logoType
                        )
                        viewModel.updateBrandProfile(updatedProfile)
                        Toast.makeText(context, "Brand Profile saved successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("save_profile_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Brand Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            // MY STUDIO / DOWNLOADS HISTORY GALLERY VIEW
            if (downloadedHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Downloads History Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Any poster you edit and click \"Download\" or \"Share\" will be saved in your Studio here for immediate access during this session.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Text(
                        text = "Session Creations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    Text(
                        text = "The following customized marketing graphics were successfully compiled in high-definition:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(downloadedHistory) { item ->
                            val resId = remember(item.drawableResName) {
                                context.resources.getIdentifier(item.drawableResName, "drawable", context.packageName)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .background(Color.DarkGray)
                                    ) {
                                        if (resId != 0) {
                                            androidx.compose.foundation.Image(
                                                painter = painterResource(id = resId),
                                                contentDescription = item.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        // Slogan waterstamped at the bottom
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = item.sloganText,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 7.sp, color = Color.White),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        // Category badge overlay
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = item.category,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val timeStr = remember(item.timestamp) {
                                            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                            sdf.format(Date(item.timestamp))
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Saved at $timeStr",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Saved successfully",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(12.dp)
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
}
