package com.tolongtukar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tolongtukar.app.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextHolder.context = applicationContext
        initKoin()
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
