package com.tolongtukar.app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.tolongtukar.app.ContextHolder
import com.tolongtukar.app.SettingsKeys
import com.tolongtukar.app.SettingsStorage

private const val SKU_REMOVE_ADS = "remove_ads"
private const val TAG = "PurchaseManager"

actual class PurchaseManager : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private var successCallback: (() -> Unit)? = null
    private var errorCallback: ((String) -> Unit)? = null
    private var isServiceConnected = false

    private fun acknowledgeAndUnlock(purchase: Purchase, onComplete: (() -> Unit)? = null) {
        if (purchase.isAcknowledged) {
            SettingsStorage().putBoolean(SettingsKeys.IS_PRO, true)
            onComplete?.invoke()
            return
        }
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient?.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                SettingsStorage().putBoolean(SettingsKeys.IS_PRO, true)
                onComplete?.invoke()
            } else {
                errorCallback?.invoke("Purchase acknowledgement failed: ${result.debugMessage}")
            }
        }
    }

    actual fun initialize() {
        if (billingClient != null) return
        val context = ContextHolder.context
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()
        // Connection starts on the first status query or purchase request.
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isServiceConnected = true
                    android.util.Log.d(TAG, "Billing service connected")
                } else {
                    android.util.Log.e(TAG, "Billing setup failed: ${billingResult.responseCode}")
                }
            }

            override fun onBillingServiceDisconnected() {
                isServiceConnected = false
                android.util.Log.d(TAG, "Billing service disconnected")
            }
        })
    }

    actual fun purchaseRemoveAds(
        activity: Any?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Resolve Activity from ContextHolder (set in MainActivity.onCreate)
        val act = ContextHolder.activity
            ?: activity as? Activity
            ?: run {
                onError("No activity available for billing")
                return
            }

        successCallback = onSuccess
        errorCallback = onError

        if (!isServiceConnected) {
            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        isServiceConnected = true
                        queryAndLaunch(act)
                    } else {
                        onError("Billing unavailable: ${billingResult.responseCode}")
                    }
                }

                override fun onBillingServiceDisconnected() {
                    isServiceConnected = false
                }
            })
        } else {
            queryAndLaunch(act)
        }
    }

    private fun queryAndLaunch(activity: Activity) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SKU_REMOVE_ADS)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                errorCallback?.invoke("Failed to load product: ${billingResult.debugMessage}")
                return@queryProductDetailsAsync
            }
            if (productDetailsList.isEmpty()) {
                errorCallback?.invoke("Product 'remove_ads' not found. Add it in Play Console.")
                return@queryProductDetailsAsync
            }

            val productDetails = productDetailsList[0]
            // For INAPP (one-time) products, the offerToken is on ProductDetails directly
            // in billing 7.x. OneTimePurchaseOfferDetails only has formattedPrice/priceAmountMicros.
            // The standard approach: build params without explicit offerToken for INAPP.
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            val result = billingClient?.launchBillingFlow(activity, flowParams)
            if (result?.responseCode != BillingClient.BillingResponseCode.OK) {
                errorCallback?.invoke("Launch failed: ${result?.debugMessage}")
            }
        }
    }

    actual fun checkPurchaseStatus(onResult: (Boolean) -> Unit) {
        fun queryPurchases() {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    onResult(SettingsStorage().getBoolean(SettingsKeys.IS_PRO, false))
                    return@queryPurchasesAsync
                }
                val validPurchases = purchases.filter {
                    it.products.contains(SKU_REMOVE_ADS) &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                validPurchases.forEach { acknowledgeAndUnlock(it) }
                val hasPro = validPurchases.isNotEmpty()
                SettingsStorage().putBoolean(SettingsKeys.IS_PRO, hasPro)
                onResult(hasPro)
            }
        }

        if (isServiceConnected) {
            queryPurchases()
        } else {
            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        isServiceConnected = true
                        queryPurchases()
                    } else {
                        onResult(SettingsStorage().getBoolean(SettingsKeys.IS_PRO, false))
                    }
                }
                override fun onBillingServiceDisconnected() {
                    isServiceConnected = false
                }
            }) ?: onResult(SettingsStorage().getBoolean(SettingsKeys.IS_PRO, false))
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val validPurchase = purchases?.firstOrNull {
                    it.products.contains(SKU_REMOVE_ADS) &&
                    it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                if (validPurchase != null) {
                    acknowledgeAndUnlock(validPurchase) { successCallback?.invoke() }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                errorCallback?.invoke("Purchase cancelled")
            }
            else -> {
                errorCallback?.invoke("Purchase failed: ${billingResult.debugMessage}")
            }
        }
    }

    actual fun destroy() {
        billingClient?.endConnection()
        billingClient = null
    }
}
