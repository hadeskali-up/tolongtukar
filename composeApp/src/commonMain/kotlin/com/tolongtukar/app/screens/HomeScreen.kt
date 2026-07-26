package com.tolongtukar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage
import com.tolongtukar.app.converter.UnitDefinitions
import com.tolongtukar.app.navigation.Screen

private data class CategoryTile(
    val categoryId: String,
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    darkMode: Boolean,
    followSystem: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleFollowSystem: (Boolean) -> Unit,
    settings: SettingsStorage
) {
    val defaultTiles = remember { buildCategoryTiles() }

    val savedOrderStr = remember { settings.getString(SettingsKeys.CATEGORY_ORDER, "") }
    val initialOrder = remember {
        if (savedOrderStr.isNotEmpty()) {
            val saved = savedOrderStr.split(",").filter { it.isNotEmpty() }
            val all = defaultTiles.map { it.categoryId }
            val merged = saved.filter { it in all } + all.filter { it !in saved }
            merged.mapNotNull { id -> defaultTiles.find { it.categoryId == id } }
        } else {
            defaultTiles
        }
    }

    var tiles by remember { mutableStateOf(initialOrder) }
    var editMode by remember { mutableStateOf(false) }

    // Drag state — track by ITEM ID (stable), not index
    var draggingTileId by remember { mutableStateOf<String?>(null) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val columns = 3

    fun saveOrder(order: List<String>) {
        settings.putString(SettingsKeys.CATEGORY_ORDER, order.joinToString(","))
    }

    fun moveItem(from: Int, to: Int) {
        if (from < 0 || to < 0 || from >= tiles.size || to >= tiles.size || from == to) return
        val mutable = tiles.toMutableList()
        val item = mutable.removeAt(from)
        mutable.add(to, item)
        tiles = mutable
        saveOrder(mutable.map { it.categoryId })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TolongTukar", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { editMode = !editMode }) {
                        Icon(
                            if (editMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (editMode) "Done reordering" else "Reorder categories",
                            tint = if (editMode) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (followSystem) {
                        IconButton(onClick = { onToggleFollowSystem(false) }) {
                            Icon(Icons.Default.BrightnessAuto, contentDescription = "Follow system (tap to override)")
                        }
                    } else {
                        IconButton(onClick = { onToggleDarkMode(!darkMode) }) {
                            Icon(
                                if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (darkMode) "Switch to light" else "Switch to dark"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val tileWidthPx = with(density) { 120.dp.toPx() }
        val tileHeightPx = with(density) { 120.dp.toPx() }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(tiles, key = { _, tile -> tile.categoryId }) { index, tile ->
                val isDragging = draggingTileId == tile.categoryId

                // CRITICAL: pointerInput key = categoryId (stable, won't restart on list change)
                val dragModifier = if (editMode) {
                    Modifier.pointerInput(tile.categoryId) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingTileId = tile.categoryId
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDragEnd = {
                                draggingTileId = null
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDragCancel = {
                                draggingTileId = null
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                dragOffsetY += dragAmount.y

                                // Look up CURRENT index dynamically
                                val currentIdx = tiles.indexOf(tile)
                                if (currentIdx < 0) return@detectDragGesturesAfterLongPress

                                val deltaCol = (dragOffsetX / tileWidthPx).toInt()
                                val deltaRow = (dragOffsetY / tileHeightPx).toInt()
                                val delta = deltaRow * columns + deltaCol

                                if (delta != 0) {
                                    val target = currentIdx + delta
                                    if (target in tiles.indices && target != currentIdx) {
                                        moveItem(currentIdx, target)
                                        // Subtract consumed amount — keep residual for smooth continued drag
                                        val consumedCol = deltaCol * tileWidthPx
                                        val consumedRow = deltaRow * tileHeightPx
                                        dragOffsetX -= consumedCol
                                        dragOffsetY -= consumedRow
                                    }
                                }
                            }
                        )
                    }
                } else {
                    Modifier
                }

                CategoryCard(
                    name = tile.name,
                    icon = tile.icon,
                    editMode = editMode,
                    isDragging = isDragging,
                    onClick = {
                        if (!editMode) onNavigate(Screen.Converter(tile.categoryId))
                    },
                    modifier = dragModifier.then(
                        if (isDragging) Modifier.graphicsLayer { alpha = 0.6f }
                        else Modifier
                    )
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    name: String,
    icon: ImageVector,
    editMode: Boolean,
    isDragging: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        enabled = !editMode,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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

            if (editMode) {
                Icon(
                    Icons.Default.DragIndicator,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
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
