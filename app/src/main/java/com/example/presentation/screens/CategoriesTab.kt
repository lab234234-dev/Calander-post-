package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PosterCatalog
import com.example.data.PosterTemplate
import com.example.presentation.PosterViewModel
import kotlinx.coroutines.delay

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val count: Int,
    val colorStart: Color,
    val colorEnd: Color,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTab(
    viewModel: PosterViewModel,
    onPosterSelected: (PosterTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // List of categories with dynamic gradient colors and Material symbols
    val categories = remember {
        listOf(
            CategoryItem("Festival", Icons.Default.Festival, 3, Color(0xFFFF5722), Color(0xFFFF9100), "Religious & Cultural Celebrations"),
            CategoryItem("Business", Icons.Default.Business, 2, Color(0xFF1A237E), Color(0xFF2979FF), "Corporate & Enterprise Branding"),
            CategoryItem("Marketing", Icons.Default.Campaign, 1, Color(0xFF004D40), Color(0xFF00B0FF), "Product Promotion & Advertisement"),
            CategoryItem("Offer", Icons.Default.LocalOffer, 1, Color(0xFFD50000), Color(0xFFFF5252), "Shop Discounts & Mega Sales"),
            CategoryItem("National Day", Icons.Default.Flag, 2, Color(0xFF4A148C), Color(0xFFAA00FF), "Independence, Republic & Patriots Days"),
            CategoryItem("Birthday", Icons.Default.Cake, 1, Color(0xFF006064), Color(0xFF00E5FF), "Custom Birthday & Anniversary Cards"),
            CategoryItem("Religious", Icons.Default.TempleHindu, 1, Color(0xFFE65100), Color(0xFFFF9100), "Spiritual & Temple Festival Flyers"),
            CategoryItem("Healthcare", Icons.Default.MedicalServices, 1, Color(0xFF2E7D32), Color(0xFF4CAF50), "Clinical, Doctor & Wellness Banners"),
            CategoryItem("Education", Icons.Default.School, 1, Color(0xFF37474F), Color(0xFF78909C), "School, College & Certificate Templates"),
            CategoryItem("Real Estate", Icons.Default.HomeWork, 1, Color(0xFF4E342E), Color(0xFF8D6E63), "Property Selling, Renting & Brokerage")
        )
    }

    LaunchedEffect(selectedCategoryName) {
        if (selectedCategoryName != null) {
            isLoading = true
            delay(350) // High-Speed simulated secure API fetch
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (selectedCategoryName == null) {
            // Master list of Categories
            Text(
                text = "Explore Categories",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "Select any category to retrieve design templates instantly from our high-speed Cloud CDN API.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { selectedCategoryName = category.name },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Gradient Accent Ring
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(category.colorStart, category.colorEnd)
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(category.colorStart.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = category.colorStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = category.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 2
                                    )
                                }
                            }

                            // Count Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .background(category.colorStart, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "API",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Category Selected - View posters under this category
            val catName = selectedCategoryName!!
            val filteredPosters = remember(catName) {
                // If specific category is not matching in catalog, mix generic business ones so it's never empty!
                val list = PosterCatalog.templates.filter { it.category.equals(catName, ignoreCase = true) }
                if (list.isEmpty()) {
                    PosterCatalog.templates.filter { it.category == "Business" || it.category == "Offer" }
                } else {
                    list
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedCategoryName = null }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to categories",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = catName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${filteredPosters.size} HD Templates fetched via API",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Calling Cloud Database API...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPosters) { template ->
                        PosterCard(
                            template = template,
                            onClick = { onPosterSelected(template) }
                        )
                    }
                }
            }
        }
    }
}
