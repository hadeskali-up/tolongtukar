package com.tolongtukar.app.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.tolongtukar.app.R

@Composable
actual fun SplashLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.mipmap.ic_launcher_foreground),
        contentDescription = "TolongTukar",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
