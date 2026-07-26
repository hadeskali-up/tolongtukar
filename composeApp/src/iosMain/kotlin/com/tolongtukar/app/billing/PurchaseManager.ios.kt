package com.tolongtukar.app.billing

import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage

/**
 * iOS implementation — no Google Play Billing.
 * Uses StoreKit when iOS in-app purchase is implemented.
 * For now, reads local IS_PRO setting only.
 */
actual class PurchaseManager {
    actual fun initialize() {
        // No-op on iOS (StoreKit not yet wired up)
    }

    actual fun purchaseRemoveAds(
        activity: Any?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // iOS: implement with StoreKit when ready
        onError("In-app purchase not yet available on iOS")
    }

    actual fun checkPurchaseStatus(onResult: (Boolean) -> Unit) {
        val settings = SettingsStorage()
        onResult(settings.getBoolean(SettingsKeys.IS_PRO, false))
    }

    actual fun destroy() {
        // No-op
    }
}
