package com.tolongtukar.app.converter

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForexResponse(
    val base: String = "USD",
    val rates: Map<String, Double> = emptyMap(),
    @SerialName("last_updated") val lastUpdated: String = "",
    @SerialName("fetched_at") val fetchedAt: String = ""
)

/**
 * Fetches forex rates from server cache (https://alisuhari.top/forex.json).
 * Server updates daily at 6AM via systemd timer; app just reads the cached JSON.
 * This keeps the ExchangeRate API key on the server and within free tier limits.
 */
object ForexService {
    private const val URL = "https://alisuhari.top/forex.json"

    private val client by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(json = kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    /**
     * Fetch cached forex rates from server.
     * Returns null on failure (caller falls back to static rates).
     */
    suspend fun fetchRates(): ForexResponse? = withContext(Dispatchers.IO) {
        try {
            client.get(URL).body<ForexResponse>()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Update CurrencyConverter with live rates from server.
     * Returns the last-updated timestamp string, or null if fetch failed.
     */
    suspend fun updateRates(): String? {
        val response = fetchRates() ?: return null
        if (response.rates.isNotEmpty()) {
            CurrencyConverter.updateRates(response.rates, response.lastUpdated)
        }
        return response.lastUpdated.ifEmpty { null }
    }
}
