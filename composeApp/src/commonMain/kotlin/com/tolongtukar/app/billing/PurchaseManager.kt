package com.tolongtukar.app.billing

/**
 * Callback for purchase flow result.
 */
expect class PurchaseManager() {
    /** Initialize billing client. Call from Activity.onCreate. */
    fun initialize()

    /**
     * Launch the purchase flow for "remove_ads" SKU.
     * @param activity The current Android Activity (cast to Any for KMP compatibility).
     * @param onSuccess Called when purchase is verified.
     * @param onError Called with error message on failure.
     */
    fun purchaseRemoveAds(
        activity: Any?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /** Check if user has already purchased (queries Play Store). */
    fun checkPurchaseStatus(
        onResult: (isPro: Boolean) -> Unit
    )

    /** Clean up billing client. Call from Activity.onDestroy. */
    fun destroy()
}
