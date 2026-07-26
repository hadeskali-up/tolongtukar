package com.tolongtukar.app.converter

/**
 * Currency conversion with hardcoded fallback rates (USD-based).
 * Approximate 2024 rates. Can be updated later with a live API.
 *
 * Each currency has a rate relative to USD (how many units = 1 USD).
 * Conversion: amount_in_target = amount_in_source × (usd_rate_target / usd_rate_source)
 */
object CurrencyConverter {

    // Rate = how many units of this currency = 1 USD
    private val rates: Map<String, Double> = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "JPY" to 149.50,
        "GBP" to 0.79,
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
        "RON" to 4.55,
        "IDR" to 15850.0,
        "THB" to 35.80,
        "PHP" to 56.20,
        "MYR" to 4.68,
        "HKD" to 7.82,
        "SGD" to 1.34,
        "NZD" to 1.66,
        "ILS" to 3.78,
        "ISK" to 138.0
    )

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
        "RON" to "Romanian Leu",
        "IDR" to "Indonesian Rupiah",
        "THB" to "Thai Baht",
        "PHP" to "Philippine Peso",
        "MYR" to "Malaysian Ringgit",
        "HKD" to "Hong Kong Dollar",
        "SGD" to "Singapore Dollar",
        "NZD" to "New Zealand Dollar",
        "ILS" to "Israeli Shekel",
        "ISK" to "Icelandic Krona"
    )

    /**
     * Build UnitDef list for the Currency category.
     * Each currency's factor = its USD rate (so base USD = 1.0).
     */
    fun currencyUnits(): List<UnitDef> {
        return rates.entries.sortedBy { it.key }.map { (code, rate) ->
            UnitDef(
                id = code,
                name = currencyNames[code] ?: code,
                symbol = code,
                strategy = ConversionStrategy.FactorStrategy(rate)
            )
        }
    }

    /**
     * Convert [amount] from [fromCurrency] to [toCurrency].
     */
    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val fromRate = rates[fromCurrency] ?: return 0.0
        val toRate = rates[toCurrency] ?: return 0.0
        if (fromRate == 0.0) return 0.0
        return amount * (toRate / fromRate)
    }

    val currencyCodes: List<String> get() = rates.keys.sorted()
}
