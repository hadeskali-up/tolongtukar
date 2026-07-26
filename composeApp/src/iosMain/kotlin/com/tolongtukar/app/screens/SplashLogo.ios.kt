package com.tolongtukar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun SplashLogo(modifier: Modifier = Modifier) {
    // iOS: placeholder until native icon asset is added to bundle
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            "TT",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A3A5C)
        )
    }
}
