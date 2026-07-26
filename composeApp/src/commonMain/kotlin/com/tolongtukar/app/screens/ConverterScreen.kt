package com.tolongtukar.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tolongtukar.app.converter.ConversionResult
import com.tolongtukar.app.converter.ConversionEngine
import com.tolongtukar.app.converter.UnitDefinitions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(category: String, onBack: () -> Unit) {
    val cat = remember(category) { UnitDefinitions.getCategory(category) }
    val units = remember(category) { cat?.units ?: emptyList() }
    val isStringBased = remember(category) { cat?.isStringBased == true }

    var inputValue by remember { mutableStateOf("") }
    var fromUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var toUnitId by remember { mutableStateOf(units.getOrElse(1) { units.firstOrNull() }?.id ?: "") }

    // Reset unit selections when category changes
    LaunchedEffect(category) {
        if (units.isNotEmpty()) {
            fromUnitId = units.first().id
            toUnitId = units.getOrElse(1) { units.first() }.id
            inputValue = ""
        }
    }

    val fromUnit = units.find { it.id == fromUnitId }
    val toUnit = units.find { it.id == toUnitId }

    // Compute result reactively
    val result = remember(inputValue, fromUnitId, toUnitId, isStringBased) {
        if (cat == null || fromUnit == null || toUnit == null) {
            ConversionResult.Text("—")
        } else if (inputValue.isBlank()) {
            ConversionResult.Text("")
        } else if (isStringBased) {
            ConversionEngine.convertString(cat.id, fromUnitId, toUnitId, inputValue)
        } else {
            val numericValue = inputValue.toDoubleOrNull()
            if (numericValue == null) {
                ConversionResult.Text("Invalid input")
            } else {
                ConversionEngine.convert(cat.id, fromUnitId, toUnitId, numericValue)
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Input field ──
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Enter value") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = if (isStringBased) KeyboardType.Text else KeyboardType.Decimal
                )
            )

            // ── From unit dropdown ──
            UnitDropdown(
                label = "From",
                selectedId = fromUnitId,
                units = units,
                onSelected = { fromUnitId = it }
            )

            // ── Swap button ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val tmp = fromUnitId
                        fromUnitId = toUnitId
                        toUnitId = tmp
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.SwapVert,
                        contentDescription = "Swap units",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // ── To unit dropdown ──
            UnitDropdown(
                label = "To",
                selectedId = toUnitId,
                units = units,
                onSelected = { toUnitId = it }
            )

            Spacer(Modifier.height(8.dp))

            // ── Result display ──
            ResultCard(
                inputValue = inputValue,
                fromSymbol = fromUnit?.symbol ?: "",
                toSymbol = toUnit?.symbol ?: "",
                result = result
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    label: String,
    selectedId: String,
    units: List<com.tolongtukar.app.converter.UnitDef>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = units.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.let { "${it.name} (${it.symbol})" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(unit.name, fontWeight = FontWeight.Medium)
                            if (unit.symbol.isNotEmpty()) {
                                Text(
                                    unit.symbol,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelected(unit.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ResultCard(
    inputValue: String,
    fromSymbol: String,
    toSymbol: String,
    result: ConversionResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Result",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(12.dp))

            val resultText = when (result) {
                is ConversionResult.Number -> result.formatted
                is ConversionResult.Text -> result.value.ifEmpty { "—" }
            }

            Text(
                text = resultText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (toSymbol.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    toSymbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
