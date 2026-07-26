package com.tolongtukar.app.converter

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Currency conversion with server-updated rates (USD-based).
 * Static fallback rates used when offline. Server updates daily at 6AM.
 *
 * rate = "how many units = 1 USD" (e.g. MYR rate 4.09 means 1 USD = 4.09 MYR)
 * factor = 1 / rate (how many USD = 1 unit of this currency)
 */
object CurrencyConverter {

    // Mutable — updated by ForexService when server rates are fetched
    private var rates: Map<String, Double> = mapOf(
        "USD" to 1.0,
        "EUR" to 0.8788,
        "JPY" to 163.81,
        "GBP" to 0.7507,
        "CNY" to 7.24,
        "AUD" to 1.52,
        "CAD" to 1.36,
        "CHF" to 0.88,
        "SEK" to 10.48,
        "NOK" to 10.79,
        "DKK" to 6.88,
        "KRW" to 1310.0,
        "MXN" to 17.05,
        "INR" to 83.25,
        "BRL" to 4.95,
        "ZAR" to 18.50,
        "TRY" to 28.95,
        "PLN" to 4.05,
        "CZK" to 23.10,
        "HUF" to 360.0,
        "IDR" to 15850.0,
        "THB" to 35.80,
        "PHP" to 56.20,
        "MYR" to 4.0909,
        "HKD" to 7.82,
        "SGD" to 1.34,
        "NZD" to 1.66,
        "AED" to 3.67,
        "SAR" to 3.75,
        "PKR" to 278.0
    )

    private var lastUpdated: String = "Offline (static rates)"

    private val mutex = Mutex()

    private val currencyNames: Map<String, String> = mapOf(
        "USD" to "US Dollar",
        "EUR" to "Euro",
        "JPY" to "Japanese Yen",
        "GBP" to "British Pound",
        "CNY" to "Chinese Yuan",
        "AUD" to "Australian Dollar",
        "CAD" to "Canadian Dollar",
        "CHF" to "Swiss Franc",
        "SEK" to "Swedish Krona",
        "NOK" to "Norwegian Krone",
        "DKK" to "Danish Krone",
        "KRW" to "South Korean Won",
        "MXN" to "Mexican Peso",
        "INR" to "Indian Rupee",
        "BRL" to "Brazilian Real",
        "ZAR" to "South African Rand",
        "TRY" to "Turkish Lira",
        "PLN" to "Polish Zloty",
        "CZK" to "Czech Koruna",
        "HUF" to "Hungarian Forint",
        "IDR" to "Indonesian Rupiah",
        "THB" to "Thai Baht",
        "PHP" to "Philippine Peso",
        "MYR" to "Malaysian Ringgit",
        "HKD" to "Hong Kong Dollar",
        "SGD" to "Singapore Dollar",
        "NZD" to "New Zealand Dollar",
        "AED" to "UAE Dirham",
        "SAR" to "Saudi Riyal",
        "PKR" to "Pakistani Rupee"
    )

    fun getLastUpdated(): String = lastUpdated

    /**
     * Update rates from server data. Called by ForexService.
     */
    suspend fun updateRates(newRates: Map<String, Double>, timestamp: String) {
        mutex.withLock {
            // Only update currencies we know about (merge: keep names, replace rates)
            val merged = rates.toMutableMap()
            for ((code, rate) in newRates) {
                if (currencyNames.containsKey(code) || code in rates) {
                    merged[code] = rate
                }
            }
            rates = merged
            lastUpdated = timestamp
        }
    }

    /**
     * Build UnitDef list for the Currency category.
     * factor = 1 / rate (how many USD = 1 of this currency)
     */
    fun currencyUnits(): List<UnitDef> {
        return rates.entries.sortedBy { it.key }.map { (code, rate) ->
            UnitDef(
                id = code,
                name = currencyNames[code] ?: code,
                symbol = code,
                strategy = ConversionStrategy.FactorStrategy(1.0 / rate)
            )
        }
    }

    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val fromRate = rates[fromCurrency] ?: return 0.0
        val toRate = rates[toCurrency] ?: return 0.0
        if (fromRate == 0.0) return 0.0
        return amount * (toRate / fromRate)
    }

    val currencyCodes: List<String> get() = rates.keys.sorted()
}
