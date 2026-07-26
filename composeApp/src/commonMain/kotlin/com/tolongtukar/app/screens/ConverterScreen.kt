package com.tolongtukar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage
import com.tolongtukar.app.converter.ConversionEngine
import com.tolongtukar.app.converter.CurrencyConverter
import com.tolongtukar.app.converter.ForexService
import com.tolongtukar.app.converter.UnitDefinitions
import com.tolongtukar.app.converter.UnitDef
import kotlinx.coroutines.launch

/**
 * ConverterNOW-style screen: ALL units visible simultaneously as a list.
 * Type in any unit's text box → all other boxes update live.
 * Long-press + drag any row to reorder. Order persists across restarts.
 * For currency: fetches live rates from server on open.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    category: String,
    onBack: () -> Unit,
    settings: SettingsStorage
) {
    val scope = rememberCoroutineScope()
    val cat = remember(category) { UnitDefinitions.getCategory(category) }
    val isStringBased = remember(category) { cat?.isStringBased == true }
    val isCurrency = category == "currency"

    // Load saved unit order, or use default
    val defaultOrder = remember(category) { cat?.units?.map { it.id } ?: emptyList() }
    val savedOrderStr = remember(category) {
        settings.getString(SettingsKeys.UNIT_ORDER_PREFIX + category, "")
    }
    val initialOrder = remember(category) {
        if (savedOrderStr.isNotEmpty()) {
            val saved = savedOrderStr.split(",").filter { it.isNotEmpty() }
            val all = cat?.units?.map { it.id } ?: emptyList()
            val merged = saved.filter { it in all } + all.filter { it !in saved }
            merged
        } else {
            defaultOrder
        }
    }

    var unitOrder by remember(category) { mutableStateOf(initialOrder) }
    var values by remember(category) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var activeUnitId by remember(category) { mutableStateOf(unitOrder.firstOrNull() ?: "") }
    var editMode by remember { mutableStateOf(false) }
    var currencyTimestamp by remember { mutableStateOf(CurrencyConverter.getLastUpdated()) }

    // Drag state
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()

    // For currency: fetch live rates from server on screen open
    LaunchedEffect(category) {
        if (isCurrency) {
            scope.launch {
                val ts = ForexService.updateRates()
                if (ts != null) {
                    currencyTimestamp = ts
                    // Refresh values with new rates
                    if (activeUnitId.isNotEmpty()) {
                        val input = values[activeUnitId] ?: "1"
                        val numVal = input.toDoubleOrNull() ?: 1.0
                        val results = ConversionEngine.convertToAll("currency", activeUnitId, numVal)
                        values = results.toMutableMap().apply { put(activeUnitId, input) }
                    }
                }
            }
        }
    }

    // Initialize: set first unit to "1", rest computed
    LaunchedEffect(category) {
        if (unitOrder.isNotEmpty()) {
            activeUnitId = unitOrder.first()
            val firstUnitId = unitOrder.first()
            if (isStringBased) {
                values = ConversionEngine.convertStringToAll(cat!!.id, firstUnitId, "1")
            } else {
                values = ConversionEngine.convertToAll(cat!!.id, firstUnitId, 1.0)
            }
        }
    }

    fun saveOrder(order: List<String>) {
        settings.putString(SettingsKeys.UNIT_ORDER_PREFIX + category, order.joinToString(","))
    }

    fun onUnitInput(unitId: String, input: String) {
        activeUnitId = unitId
        if (cat == null) return

        if (input.isBlank()) {
            values = unitOrder.associate { it to "" }
            return
        }

        if (isStringBased) {
            val results = ConversionEngine.convertStringToAll(cat.id, unitId, input)
            values = results.toMutableMap().apply { put(unitId, input) }
        } else {
            val numericValue = input.toDoubleOrNull()
            if (numericValue == null) return
            val results = ConversionEngine.convertToAll(cat.id, unitId, numericValue)
            values = results.toMutableMap().apply { put(unitId, input) }
        }
    }

    fun moveItem(from: Int, to: Int) {
        if (from < 0 || to < 0 || from >= unitOrder.size || to >= unitOrder.size) return
        val mutable = unitOrder.toMutableList()
        val item = mutable.removeAt(from)
        mutable.add(to, item)
        unitOrder = mutable
        saveOrder(mutable)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cat?.name ?: "Converter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (unitOrder.size > 1) {
                        IconButton(onClick = { editMode = !editMode }) {
                            Icon(
                                if (editMode) Icons.Default.Done else Icons.Default.DragIndicator,
                                contentDescription = if (editMode) "Done reordering" else "Reorder units",
                                tint = if (editMode) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
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
        if (unitOrder.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No units available", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }

        val density = LocalDensity.current

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                top = if (isCurrency) 8.dp else 12.dp,
                bottom = 12.dp
            )
        ) {
            // Currency: show last-updated timestamp at top
            if (isCurrency) {
                item {
                    Text(
                        text = "Last updated: $currencyTimestamp",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            itemsIndexed(unitOrder) { index, unitId ->
                val unit = cat?.units?.find { it.id == unitId }
                if (unit != null) {
                    val unitValue = values[unitId] ?: ""
                    val isActive = unitId == activeUnitId
                    val isDragging = draggingIndex == index

                    // Drag gesture for reordering
                    val dragModifier = if (editMode) {
                        Modifier.pointerInput(unitOrder) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { draggingIndex = index },
                                onDragEnd = { draggingIndex = null; dragOffset = 0f },
                                onDragCancel = { draggingIndex = null; dragOffset = 0f },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount.y

                                    // Calculate target index based on accumulated drag
                                    val itemHeight = with(density) { 72.dp.toPx() }
                                    val delta = (dragOffset / itemHeight).toInt()

                                    if (delta != 0) {
                                        val target = index + delta
                                        if (target in unitOrder.indices && target != index) {
                                            moveItem(index, target)
                                            draggingIndex = target
                                            dragOffset = 0f
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        Modifier
                    }

                    UnitRow(
                        unitName = unit.name,
                        unitSymbol = unit.symbol,
                        value = unitValue,
                        isActive = isActive,
                        isStringBased = isStringBased,
                        editMode = editMode,
                        isDragging = isDragging,
                        modifier = dragModifier
                            .then(
                                if (isDragging) Modifier.graphicsLayer { alpha = 0.6f }
                                else Modifier
                            ),
                        onValueChange = { onUnitInput(unitId, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitRow(
    unitName: String,
    unitSymbol: String,
    value: String,
    isActive: Boolean,
    isStringBased: Boolean,
    editMode: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Drag handle (visible in edit mode)
        if (editMode) {
            Icon(
                Icons.Default.DragIndicator,
                contentDescription = "Drag to reorder",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Unit name + symbol
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = unitName,
                fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            if (unitSymbol.isNotEmpty()) {
                Text(
                    text = unitSymbol,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Text box
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = !editMode,
            modifier = Modifier.weight(1.2f),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isStringBased) KeyboardType.Text else KeyboardType.Decimal
            ),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    }
}
