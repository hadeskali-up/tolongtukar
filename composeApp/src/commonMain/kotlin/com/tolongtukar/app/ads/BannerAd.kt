package com.tolongtukar.app.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-agnostic banner ad composable.
 *
 * Currently a PLACEHOLDER — no real AdMob SDK calls.
 * Replace with actual implementation when ready to serve ads.
 */
@Composable
expect fun BannerAd(modifier: Modifier = Modifier)
