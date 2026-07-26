package com.tolongtukar.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage
import com.tolongtukar.app.billing.PurchaseManager
import com.tolongtukar.app.util.openUrl

private const val TERMS_URL = "https://alisuhari.top/tolongtukar/terms.html"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    darkMode: Boolean,
    followSystem: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleFollowSystem: (Boolean) -> Unit,
    settings: SettingsStorage,
    onProStatusChanged: () -> Unit
) {
    val purchaseManager = remember { PurchaseManager() }
    var isPro by remember { mutableStateOf(settings.getBoolean(SettingsKeys.IS_PRO, false)) }
    var isPurchasing by remember { mutableStateOf(false) }
    var purchaseError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Activity is resolved in platform code via PurchaseManager.
    // On Android, PurchaseManager reads it from ContextHolder.
    // Pass null — Android impl resolves it internally.

    LaunchedEffect(Unit) {
        purchaseManager.initialize()
        purchaseManager.checkPurchaseStatus { pro ->
            isPro = pro
        }
    }

    DisposableEffect(Unit) {
        onDispose { purchaseManager.destroy() }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Ads Removed! 🎉") },
            text = { Text("Thank you for your purchase! TolongTukar is now ad-free forever.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onProStatusChanged()
                }) { Text("Great!") }
            }
        )
    }

    purchaseError?.let { error ->
        AlertDialog(
            onDismissRequest = { purchaseError = null },
            title = { Text("Purchase Failed") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { purchaseError = null }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === APPEARANCE SECTION ===
            SectionHeader("Appearance")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column {
                    // Follow System toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.BrightnessAuto,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Follow System Theme", fontWeight = FontWeight.Medium)
                            Text(
                                "Match device light/dark setting",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = followSystem,
                            onCheckedChange = { onToggleFollowSystem(it) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Dark mode toggle (disabled if follow system)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (followSystem)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Dark Mode",
                                fontWeight = FontWeight.Medium,
                                color = if (followSystem)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { onToggleDarkMode(it) },
                            enabled = !followSystem
                        )
                    }
                }
            }

            // === REMOVE ADS SECTION ===
            SectionHeader("Premium")

            if (isPro) {
                // Already purchased — show Pro badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Premium Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Ads removed forever. Thank you! 💙",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            } else {
                // Purchase card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            "Remove Ads Forever",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Enjoy TolongTukar without any advertisements.\nOne-time purchase. No subscriptions.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                isPurchasing = true
                                purchaseManager.purchaseRemoveAds(
                                    activity = null,
                                    onSuccess = {
                                        isPro = true
                                        isPurchasing = false
                                        showSuccessDialog = true
                                    },
                                    onError = { error ->
                                        isPurchasing = false
                                        purchaseError = error
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isPurchasing
                        ) {
                            if (isPurchasing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Remove Ads — RM 4.99",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // === ABOUT SECTION ===
            SectionHeader("About")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column {
                    // App version
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text("Version", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Text(
                            "1.0.0",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Terms & Conditions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openUrl(TERMS_URL) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Terms & Conditions",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Privacy (same page, different section)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openUrl(TERMS_URL) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Privacy Policy",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Footer
            Text(
                "TolongTukar v1.0.0\nMade with ❤️ by AliWorld",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}
