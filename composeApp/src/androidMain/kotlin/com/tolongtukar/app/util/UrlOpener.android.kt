package com.tolongtukar.app.util

import android.content.Intent
import android.net.Uri
import com.tolongtukar.app.ContextHolder

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ContextHolder.context.startActivity(intent)
}
