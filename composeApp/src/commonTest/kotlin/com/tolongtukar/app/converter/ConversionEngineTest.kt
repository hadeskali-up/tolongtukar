package com.tolongtukar.app.converter

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConversionEngineTest {
    @Test
    fun currencyConversionUsesRefreshedDailyRates() = runTest {
        CurrencyConverter.updateRates(
            newRates = mapOf("USD" to 1.0, "MYR" to 5.0),
            timestamp = "test-daily-rate"
        )

        val usdToAll = ConversionEngine.convertToAll("currency", "USD", 2.0)
        assertEquals("10", usdToAll["MYR"])
        assertEquals("test-daily-rate", CurrencyConverter.getLastUpdated())
    }

    @Test
    fun currencyDirectionIsCorrectFromMyrToUsd() = runTest {
        CurrencyConverter.updateRates(
            newRates = mapOf("USD" to 1.0, "MYR" to 4.0),
            timestamp = "direction-test"
        )

        val myrToAll = ConversionEngine.convertToAll("currency", "MYR", 8.0)
        assertEquals("2", myrToAll["USD"])
    }

    @Test
    fun temperatureAndNumeralConversionsRemainCorrect() {
        val temperatures = ConversionEngine.convertToAll("temperature", "celsius", 100.0)
        assertEquals("212", temperatures["fahrenheit"])

        val numerals = ConversionEngine.convertStringToAll("numeral_systems", "decimal_numeral", "255")
        assertEquals("FF", numerals["hexadecimal"])
        assertEquals("11111111", numerals["binary"])
    }

    @Test
    fun inputFormattingDoesNotExposeFloatingPointNoise() {
        val result = ConversionEngine.convertToAll("length", "meters", 1.0)
        assertTrue(result["feet"].orEmpty().startsWith("3.28084"))
    }
}
