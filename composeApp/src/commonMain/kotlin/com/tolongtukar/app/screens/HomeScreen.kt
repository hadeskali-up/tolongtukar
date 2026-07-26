package com.tolongtukar.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tolongtukar.app.converter.UnitDefinitions
import com.tolongtukar.app.navigation.Screen

private data class CategoryTile(
    val categoryId: String,
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    val tiles = remember { buildCategoryTiles() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TolongTukar", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(tiles) { tile ->
                CategoryCard(
                    name = tile.name,
                    icon = tile.icon,
                    onClick = { onNavigate(Screen.Converter(tile.categoryId)) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(name: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = name,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun buildCategoryTiles(): List<CategoryTile> {
    val iconMap = mapOf(
        "length" to Icons.Default.Straighten,
        "area" to Icons.Default.CropSquare,
        "volume" to Icons.Default.WaterDrop,
        "mass" to Icons.Default.Scale,
        "time" to Icons.Default.Schedule,
        "speed" to Icons.Default.Speed,
        "force" to Icons.Default.Bolt,
        "fuel_consumption" to Icons.Default.LocalGasStation,
        "pressure" to Icons.Default.Compress,
        "energy" to Icons.Default.Bolt,
        "power" to Icons.Default.ElectricalServices,
        "angle" to Icons.Default.Architecture,
        "torque" to Icons.Default.Settings,
        "digital_data" to Icons.Default.Memory,
        "si_prefixes" to Icons.Default.Science,
        "density" to Icons.Default.BlurOn,
        "temperature" to Icons.Default.Thermostat,
        "numeral_systems" to Icons.Default.Code,
        "shoe_size" to Icons.AutoMirrored.Filled.DirectionsWalk,
        "currency" to Icons.Default.AttachMoney
    )
    return UnitDefinitions.categories.map { cat ->
        CategoryTile(
            categoryId = cat.id,
            name = cat.name,
            icon = iconMap[cat.id] ?: Icons.Default.Category
        )
    }
}
