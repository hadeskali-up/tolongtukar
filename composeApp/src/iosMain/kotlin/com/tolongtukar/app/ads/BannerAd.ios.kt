package com.tolongtukar.app.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * iOS implementation — no-op placeholder.
 * Wire up Google Mobile Ads SDK via CocoaPods when ready for iOS.
 */
@Composable
actual fun BannerAd(modifier: Modifier) {
    // No-op: iOS AdMob not yet integrated
    Box(modifier = modifier.fillMaxWidth().height(0.dp)) {}
}
