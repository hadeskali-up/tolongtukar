package com.tolongtukar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage
import com.tolongtukar.app.converter.ConversionEngine
import com.tolongtukar.app.converter.CurrencyConverter
import com.tolongtukar.app.converter.UnitDefinitions
import com.tolongtukar.app.converter.UnitDef

/**
 * ConverterNOW-style screen: ALL units visible simultaneously as a list.
 * Type in any unit's text box → all other boxes update live.
 * Units can be reordered via up/down arrows; order persists across restarts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    category: String,
    onBack: () -> Unit,
    settings: SettingsStorage
) {
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
            // Merge: saved order first, then any new units not in saved
            val all = cat?.units?.map { it.id } ?: emptyList()
            val merged = saved.filter { it in all } + all.filter { it !in saved }
            merged
        } else {
            defaultOrder
        }
    }

    // Mutable order (can be rearranged)
    var unitOrder by remember(category) { mutableStateOf(initialOrder) }

    // Map of unitId → text shown in that unit's text box
    var values by remember(category) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var activeUnitId by remember(category) { mutableStateOf(unitOrder.firstOrNull() ?: "") }
    var editMode by remember { mutableStateOf(false) }

    fun saveOrder(order: List<String>) {
        settings.putString(SettingsKeys.UNIT_ORDER_PREFIX + category, order.joinToString(","))
    }

    fun moveUnit(index: Int, direction: Int) {
        val newIndex = index + direction
        if (newIndex < 0 || newIndex >= unitOrder.size) return
        val mutable = unitOrder.toMutableList()
        val temp = mutable[index]
        mutable[index] = mutable[newIndex]
        mutable[newIndex] = temp
        unitOrder = mutable
        saveOrder(mutable)
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
                    // Edit/reorder toggle
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

        LazyColumn(
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
                        text = "Last updated: ${CurrencyConverter.lastUpdated}",
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

                    UnitRow(
                        unitName = unit.name,
                        unitSymbol = unit.symbol,
                        value = unitValue,
                        isActive = isActive,
                        isStringBased = isStringBased,
                        editMode = editMode,
                        canMoveUp = index > 0,
                        canMoveDown = index < unitOrder.size - 1,
                        onValueChange = { onUnitInput(unitId, it) },
                        onMoveUp = { moveUnit(index, -1) },
                        onMoveDown = { moveUnit(index, 1) }
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
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onValueChange: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Reorder controls (only visible in edit mode)
        if (editMode) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move up",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move down",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
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
