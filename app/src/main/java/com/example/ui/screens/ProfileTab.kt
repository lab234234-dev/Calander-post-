package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BrandProfile
import com.example.ui.PosterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    viewModel: PosterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val brandProfile by viewModel.brandProfile.collectAsState()

    val profile = brandProfile ?: BrandProfile()

    // Internal draft state to allow editing before saving
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
                        Icon(icon, contentDescription = null, size = 18.dp, tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant)
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
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(imageVector, contentDescription, modifier = Modifier.size(size), tint = tint)
}
