package com.tolongtukar.app.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-agnostic banner ad composable.
 *
 * Android: renders a real Google AdMob BannerView using test ad unit ID.
 * iOS: no-op placeholder (implement when iOS AdMob SDK is wired up).
 *
 * Test Ad Unit ID: ca-app-pub-3940256099942544/6300978111
 * Replace with production ID after AdMob approval.
 */
@Composable
expect fun BannerAd(modifier: Modifier = Modifier)
