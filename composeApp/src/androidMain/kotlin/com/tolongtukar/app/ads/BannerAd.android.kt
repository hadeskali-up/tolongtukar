package com.tolongtukar.app.ads

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Android implementation — renders a real AdMob banner using Google's test ad unit ID.
 *
 * Test banner ID: ca-app-pub-3940256099942544/6300978111
 * After AdMob approval, replace with your production ad unit ID.
 */
@Composable
actual fun BannerAd(modifier: Modifier) {
    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER) // 320×50 dp
            adUnitId = "ca-app-pub-3940256099942544/6300978111" // Google test banner
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { adView },
            update = { it.loadAd(AdRequest.Builder().build()) }
        )
    }
}
