package com.tolongtukar.app.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage
import com.tolongtukar.app.ads.BannerAd
import com.tolongtukar.app.converter.UnitDefinitions
import com.tolongtukar.app.navigation.Screen
import com.tolongtukar.app.theme.Navy
import com.tolongtukar.app.theme.Orange

private data class CategoryTile(
    val categoryId: String,
    val name: String,
    val icon: ImageVector,
    val chipColor: Color
)

@Composable
private fun Wordmark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SplashLogo(modifier = Modifier.size(30.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            "Tolong",
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Tukar",
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    darkMode: Boolean,
    followSystem: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleFollowSystem: (Boolean) -> Unit,
    settings: SettingsStorage,
    isPro: Boolean = false
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
                title = { Wordmark() },
                actions = {
                    IconButton(onClick = { editMode = !editMode }) {
                        Icon(
                            if (editMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (editMode) "Done reordering" else "Reorder categories",
                            tint = if (editMode) MaterialTheme.colorScheme.secondary
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
                    IconButton(onClick = { onNavigate(Screen.Settings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val tileWidthPx = with(density) { 120.dp.toPx() }
        val tileHeightPx = with(density) { 120.dp.toPx() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(tiles, key = { _, tile -> tile.categoryId }) { index, tile ->
                val isDragging = draggingTileId == tile.categoryId

                // --- SPRING ANIMATIONS ---
                val animScale by animateFloatAsState(
                    targetValue = if (isDragging) 1.08f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "scale"
                )
                val animShadow by animateFloatAsState(
                    targetValue = if (isDragging) 14f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "shadow"
                )
                val animAlpha by animateFloatAsState(
                    targetValue = if (isDragging) 0.85f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "alpha"
                )

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

                                val currentIdx = tiles.indexOf(tile)
                                if (currentIdx < 0) return@detectDragGesturesAfterLongPress

                                val deltaCol = (dragOffsetX / tileWidthPx).toInt()
                                val deltaRow = (dragOffsetY / tileHeightPx).toInt()
                                val delta = deltaRow * columns + deltaCol

                                if (delta != 0) {
                                    val target = currentIdx + delta
                                    if (target in tiles.indices && target != currentIdx) {
                                        moveItem(currentIdx, target)
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
                    chipColor = tile.chipColor,
                    editMode = editMode,
                    isDragging = isDragging,
                    animScale = animScale,
                    animShadow = animShadow,
                    animAlpha = animAlpha,
                    onClick = {
                        if (!editMode) onNavigate(Screen.Converter(tile.categoryId))
                    },
                    modifier = dragModifier
                        .then(Modifier.animateItem())
                )
            }
        }

        // AdMob banner removed from Home — placeholder shown in ConverterScreen instead
        }
    }
}

@Composable
private fun CategoryCard(
    name: String,
    icon: ImageVector,
    chipColor: Color,
    editMode: Boolean,
    isDragging: Boolean,
    animScale: Float,
    animShadow: Float,
    animAlpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isDragging)
        MaterialTheme.colorScheme.secondary
    else
        MaterialTheme.colorScheme.outline

    Surface(
        onClick = onClick,
        enabled = !editMode,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(animShadow.dp, RoundedCornerShape(16.dp))
            .graphicsLayer {
                scaleX = animScale
                scaleY = animScale
                alpha = animAlpha
            },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(chipColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = name,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    minLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (editMode) {
                Icon(
                    Icons.Default.DragIndicator,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun buildCategoryTiles(): List<CategoryTile> {
    // Distinct icons per category — no duplicates. Tinted chips rotate navy/orange family.
    val navyChip = Color(0xFFE4EDF6)    // soft navy tint
    val orangeChip = Color(0xFFFDEBD7)  // soft orange tint
    val tealChip = Color(0xFFE0F0EC)    // soft teal tint
    val plumChip = Color(0xFFF1E7F0)    // soft plum tint

    val iconMap = mapOf(
        "length" to (Icons.Default.Straighten to navyChip),
        "area" to (Icons.Default.CropSquare to orangeChip),
        "volume" to (Icons.Default.WaterDrop to tealChip),
        "mass" to (Icons.Default.Scale to plumChip),
        "time" to (Icons.Default.Schedule to navyChip),
        "speed" to (Icons.Default.Speed to orangeChip),
        "force" to (Icons.Default.Bolt to tealChip),
        "fuel_consumption" to (Icons.Default.LocalGasStation to plumChip),
        "pressure" to (Icons.Default.Compress to navyChip),
        "energy" to (Icons.Default.WbSunny to orangeChip),
        "power" to (Icons.Default.ElectricalServices to tealChip),
        "angle" to (Icons.Default.Architecture to plumChip),
        "torque" to (Icons.Default.Cached to navyChip),
        "digital_data" to (Icons.Default.Memory to orangeChip),
        "si_prefixes" to (Icons.Default.Science to tealChip),
        "density" to (Icons.Default.BlurOn to plumChip),
        "temperature" to (Icons.Default.Thermostat to navyChip),
        "numeral_systems" to (Icons.Default.Code to orangeChip),
        "shoe_size" to (Icons.AutoMirrored.Filled.DirectionsWalk to tealChip),
        "currency" to (Icons.Default.AttachMoney to plumChip)
    )
    return UnitDefinitions.categories.map { cat ->
        val (icon, chip) = iconMap[cat.id] ?: (Icons.Default.Category to navyChip)
        CategoryTile(
            categoryId = cat.id,
            name = cat.name,
            icon = icon,
            chipColor = chip
        )
    }
}
