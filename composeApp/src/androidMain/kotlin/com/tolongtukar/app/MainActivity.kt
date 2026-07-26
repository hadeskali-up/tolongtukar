package com.tolongtukar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import com.tolongtukar.app.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12+ system splash screen (shows logo instantly on cold start)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        ContextHolder.context = applicationContext
        ContextHolder.activity = this
        initKoin()
        enableEdgeToEdge()

        // Initialize Google AdMob SDK
        MobileAds.initialize(this) {}

        // Keep system splash visible until Compose is ready
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            App()
            isReady = true
        }
    }
}
