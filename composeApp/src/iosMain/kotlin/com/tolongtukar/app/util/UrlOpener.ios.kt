package com.tolongtukar.app.util

import platform.UIKit.UIApplication
import platform.Foundation.NSURL

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    nsUrl?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}
