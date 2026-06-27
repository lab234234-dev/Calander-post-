package com.example.presentation.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.BrandProfile
import com.example.data.PosterTemplate
import com.example.presentation.PosterGenerator
import com.example.presentation.PosterViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorTab(
    viewModel: PosterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedPoster by viewModel.selectedPoster.collectAsState()
    val brandProfile by viewModel.brandProfile.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val showBrandName by viewModel.showBrandName.collectAsState()
    val showMobileNumber by viewModel.showMobileNumber.collectAsState()
    val showAddress by viewModel.showAddress.collectAsState()
    val showSocialHandle by viewModel.showSocialHandle.collectAsState()
    val showLogo by viewModel.showLogo.collectAsState()
    val showQrCode by viewModel.showQrCode.collectAsState()

    val customSloganText by viewModel.customSloganText.collectAsState()
    val customColorHex by viewModel.customColorHex.collectAsState()

    val isFav by if (selectedPoster != null) {
        viewModel.isPosterFav(selectedPoster!!.id).collectAsState(initial = false)
    } else {
        remember { mutableStateOf(false) }
    }

    if (selectedPoster == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select a Poster to Customize", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Go to the Calendar Tab and choose any festival or business template to begin styling your custom advertisement.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    val poster = selectedPoster!!
    val profile = brandProfile ?: BrandProfile()

    // Determine current active slogan based on selected language and any custom override
    val activeSlogan = remember(poster, selectedLanguage, customSloganText) {
        customSloganText ?: poster.slogans.find { it.language == selectedLanguage }?.text ?: poster.slogans.firstOrNull()?.text ?: "Design Your Every Day"
    }

    val resId = remember(poster.drawableResName) {
        context.resources.getIdentifier(poster.drawableResName, "drawable", context.packageName)
    }

    // List of colors for branding strip coloring
    val colorPalettes = listOf(
        "#1A237E" to "Royal Indigo",
        "#E65100" to "Vibrant Saffron",
        "#004D40" to "Emerald Green",
        "#4A148C" to "Royal Purple",
        "#880E4F" to "Wine Red",
        "#006064" to "Cyan Teal",
        "#1B5E20" to "Forest Green",
        "#212121" to "Slate Black"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Back Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectPoster(null) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Customize Poster",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Add business details and download/share",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // --- 1. Live Interactive Poster Visualizer Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .background(Color.Black)
                .testTag("poster_visualizer_container")
        ) {
            // Background Base Poster
            if (resId != 0) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = resId),
                    contentDescription = poster.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Template Base Missing", color = Color.White)
                }
            }

            // Slogan overlay (Card with semi-translucent dark backdrop and gold border)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .offset(y = (-30).dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
                    .border(1.dp, SoftGoldColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = activeSlogan,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Live Customized Brand Strip Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(88.dp)
                    .background(Color(android.graphics.Color.parseColor(profile.primaryColorHex)))
            ) {
                // Saffron Accent divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color(android.graphics.Color.parseColor(profile.textColorHex)))
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Brand Details
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (showLogo) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(profile.textColorHex)).copy(alpha = 0.2f))
                                    .border(1.dp, Color(android.graphics.Color.parseColor(profile.textColorHex)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.brandName.take(2).uppercase(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(android.graphics.Color.parseColor(profile.textColorHex))
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(verticalArrangement = Arrangement.Center) {
                            if (showBrandName) {
                                Text(
                                    text = profile.brandName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(android.graphics.Color.parseColor(profile.textColorHex))
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (profile.shopName.isNotBlank() && profile.shopName != "My Shop") profile.shopName else profile.tagline,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 9.sp,
                                        color = Color(android.graphics.Color.parseColor(profile.textColorHex)).copy(alpha = 0.8f)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Sub-row containing contacts
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                if (showMobileNumber) {
                                    Text(
                                        text = "📞 ${profile.mobileNumber}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            color = Color(android.graphics.Color.parseColor(profile.textColorHex)).copy(alpha = 0.85f)
                                        ),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                if (showSocialHandle) {
                                    Text(
                                        text = "🌐 ${profile.socialHandle}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            color = Color(android.graphics.Color.parseColor(profile.textColorHex)).copy(alpha = 0.85f)
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Right QR block
                    if (showQrCode) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Simple simulated QR scan design
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val p = w / 10f
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(p*3, p*3))
                                drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(p, p), size = androidx.compose.ui.geometry.Size(p, p))
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(w - p*3, 0f), size = androidx.compose.ui.geometry.Size(p*3, p*3))
                                drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(w - p*2, p), size = androidx.compose.ui.geometry.Size(p, p))
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(0f, w - p*3), size = androidx.compose.ui.geometry.Size(p*3, p*3))
                                drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(p, w - p*2), size = androidx.compose.ui.geometry.Size(p, p))
                                // mock pixels
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(p*5, p*5), size = androidx.compose.ui.geometry.Size(p*2, p*2))
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(p*4, p*2), size = androidx.compose.ui.geometry.Size(p, p))
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(p*7, p*4), size = androidx.compose.ui.geometry.Size(p, p))
                                drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(p*2, p*5), size = androidx.compose.ui.geometry.Size(p, p))
                            }
                        }
                    }
                }
            }

            // Floating Favorite Button
            IconButton(
                onClick = { viewModel.toggleFavorite(poster) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFav) Color.Red else Color.White
                )
            }
        }

        // --- 2. Action Controls Pane ---

        // Slogan Language Switcher
        Text(
            text = "Slogan Language",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        val languages = listOf("English", "ગુજરાતી", "हिन्दी")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLanguage == lang
                OutlinedButton(
                    onClick = { viewModel.selectLanguage(lang) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(lang, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // Choose / Edit Slogan Quotes
        Text(
            text = "Select / Edit Slogan",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Ready slogans for this poster
        val activeLanguageSlogans = remember(poster, selectedLanguage) {
            poster.slogans.filter { it.language == selectedLanguage }.map { it.text }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(activeLanguageSlogans) { sloganOption ->
                val isSelectedSlogan = customSloganText == sloganOption || (customSloganText == null && activeSlogan == sloganOption)
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(80.dp)
                        .clickable { viewModel.setCustomSlogan(sloganOption) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelectedSlogan) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (isSelectedSlogan) 2.dp else 1.dp,
                        color = if (isSelectedSlogan) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = sloganOption,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Input Custom Slogan Text Box
        OutlinedTextField(
            value = customSloganText ?: activeSlogan,
            onValueChange = { viewModel.setCustomSlogan(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("slogan_input_field"),
            label = { Text("Write Custom Slogan / Quote") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        )

        // Overlay Primary Color Picker
        Text(
            text = "Branding Strip Background Color",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(colorPalettes) { (colorHex, colorName) ->
                val isSelectedColor = profile.primaryColorHex.equals(colorHex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(colorHex)))
                        .border(
                            width = if (isSelectedColor) 3.dp else 0.dp,
                            color = if (isSelectedColor) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            viewModel.updateBrandProfile(profile.copy(primaryColorHex = colorHex))
                        }
                )
            }
        }

        // Visibility Toggles (Show/Hide Layers)
        Text(
            text = "Show / Hide Overlay Elements",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            FilterChip(
                selected = showBrandName,
                onClick = { viewModel.toggleBrandName() },
                label = { Text("Brand Name") },
                leadingIcon = { if (showBrandName) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = showMobileNumber,
                onClick = { viewModel.toggleMobileNumber() },
                label = { Text("Mobile Phone") },
                leadingIcon = { if (showMobileNumber) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = showSocialHandle,
                onClick = { viewModel.toggleSocialHandle() },
                label = { Text("Social Link") },
                leadingIcon = { if (showSocialHandle) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = showLogo,
                onClick = { viewModel.toggleLogo() },
                label = { Text("Brand Logo") },
                leadingIcon = { if (showLogo) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = showQrCode,
                onClick = { viewModel.toggleQrCode() },
                label = { Text("QR Code") },
                leadingIcon = { if (showQrCode) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        // --- 3. HD Action Buttons (Download & Share) ---
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Generate and save to gallery
                val bitmap = PosterGenerator.generatePosterBitmap(
                    context = context,
                    template = poster,
                    profile = profile,
                    sloganText = activeSlogan,
                    showBrandName = showBrandName,
                    showMobile = showMobileNumber,
                    showAddress = showAddress,
                    showSocial = showSocialHandle,
                    showLogo = showLogo,
                    showQr = showQrCode,
                    overlayColorHex = profile.primaryColorHex
                )
                savePosterToGallery(context, bitmap, poster.title)
                viewModel.recordDownload(poster, activeSlogan)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .testTag("download_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download HD Poster (Gallery)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                // Generate and share
                val bitmap = PosterGenerator.generatePosterBitmap(
                    context = context,
                    template = poster,
                    profile = profile,
                    sloganText = activeSlogan,
                    showBrandName = showBrandName,
                    showMobile = showMobileNumber,
                    showAddress = showAddress,
                    showSocial = showSocialHandle,
                    showLogo = showLogo,
                    showQr = showQrCode,
                    overlayColorHex = profile.primaryColorHex
                )
                sharePoster(context, bitmap, poster.title)
                viewModel.recordDownload(poster, activeSlogan)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .testTag("share_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Instantly (WhatsApp / Instagram)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// Helpers for Saving & Sharing
private fun savePosterToGallery(context: Context, bitmap: Bitmap, title: String) {
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
                Toast.makeText(context, "HD Poster saved to Gallery successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Error saving image stream", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Unable to create MediaStore row", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

private fun sharePoster(context: Context, bitmap: Bitmap, title: String) {
    val file = PosterGenerator.saveBitmapToTempFile(context, bitmap, "poster365_shared_art")
    if (file != null) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "com.aistudio.poster365.calendar.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Created on India's Smart Calendar App: Poster365! 📅🎨 Every Day Has a Design.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Poster with Brand Partners"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Sharing failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Failed to compile shareable poster", Toast.LENGTH_SHORT).show()
    }
}

val SoftGoldColor = Color(0xFFFFB300)
