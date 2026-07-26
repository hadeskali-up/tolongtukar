package com.tolongtukar.app.util

/**
 * Platform-agnostic URL opener.
 * Android: opens URL in default browser via Intent.
 * iOS: opens URL in Safari via UIApplication.shared.open.
 */
expect fun openUrl(url: String)
