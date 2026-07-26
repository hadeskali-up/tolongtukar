package com.tolongtukar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.tolongtukar.app.converter.ConversionEngine
import com.tolongtukar.app.converter.UnitDefinitions

/**
 * ConverterNOW-style screen: ALL units visible simultaneously as a list.
 * Type in any unit's text box → all other boxes update live.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(category: String, onBack: () -> Unit) {
    val cat = remember(category) { UnitDefinitions.getCategory(category) }
    val units = remember(category) { cat?.units ?: emptyList() }
    val isStringBased = remember(category) { cat?.isStringBased == true }

    // Map of unitId → text shown in that unit's text box
    var values by remember(category) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var activeUnitId by remember(category) { mutableStateOf(units.firstOrNull()?.id ?: "") }

    // Initialize: set first unit to "1", rest computed
    LaunchedEffect(category) {
        if (units.isNotEmpty()) {
            activeUnitId = units.first().id
            val firstUnit = units.first()
            if (isStringBased) {
                values = mapOf(firstUnit.id to "1")
                // Compute all others
                val results = ConversionEngine.convertStringToAll(cat!!.id, firstUnit.id, "1")
                values = results
            } else {
                val results = ConversionEngine.convertToAll(cat!!.id, firstUnit.id, 1.0)
                values = results
            }
        }
    }

    fun onUnitInput(unitId: String, input: String) {
        activeUnitId = unitId
        if (cat == null) return

        if (input.isBlank()) {
            // Clear all fields
            values = units.associate { it.id to "" }
            return
        }

        if (isStringBased) {
            val results = ConversionEngine.convertStringToAll(cat.id, unitId, input)
            // Keep the edited field as raw input
            values = results.toMutableMap().apply { put(unitId, input) }
        } else {
            val numericValue = input.toDoubleOrNull()
            if (numericValue == null) return
            val results = ConversionEngine.convertToAll(cat.id, unitId, numericValue)
            // Keep the edited field as raw input (no reformatting while typing)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (units.isEmpty()) {
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
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(units) { unit ->
                val unitValue = values[unit.id] ?: ""
                val isActive = unit.id == activeUnitId

                UnitRow(
                    unitName = unit.name,
                    unitSymbol = unit.symbol,
                    value = unitValue,
                    isActive = isActive,
                    isStringBased = isStringBased,
                    onValueChange = { onUnitInput(unit.id, it) }
                )
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
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Unit name + symbol (left side, fixed width)
        Column(
            modifier = Modifier.weight(0.4f),
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

        // Text box (right side, flexible width)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.weight(0.6f),
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    }
}
