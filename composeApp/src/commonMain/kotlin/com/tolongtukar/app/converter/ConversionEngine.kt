package com.tolongtukar.app.converter

import kotlin.math.*

/**
 * Result of a conversion. Either a numeric result (Double) or a string result
 * (used by numeral systems which operate on Long → string radix conversions).
 */
sealed class ConversionResult {
    data class Number(val value: Double, val formatted: String) : ConversionResult()
    data class Text(val value: String) : ConversionResult()
}

/**
 * A single unit definition. Supports three conversion strategies:
 * - [FactorStrategy]: linear conversion (value * factor), the most common.
 * - [FormulaStrategy]: arbitrary to-base/from-base lambdas (temperature, shoe size).
 * - [ReciprocalStrategy]: pace-style units where speed = 1 / value (min/km, min/mi, L/100km, mpg).
 */
sealed class ConversionStrategy {
    /** Convert using a multiplication factor relative to the base unit. */
    data class FactorStrategy(val factor: Double) : ConversionStrategy()

    /** Convert using explicit to-base / from-base formula lambdas. */
    data class FormulaStrategy(
        val toBase: (Double) -> Double,
        val fromBase: (Double) -> Double
    ) : ConversionStrategy()

    /**
     * Reciprocal strategy for pace / consumption units.
     * The unit stores its factor in the "natural" direction (e.g. seconds per km for min/km).
     * When the *user* enters a value in this unit, the true base value = factor / inputValue.
     * When converting *to* this unit, the displayed value = factor / baseValue.
     */
    data class ReciprocalStrategy(val factor: Double) : ConversionStrategy()
}

/**
 * Definition of a single unit within a category.
 */
data class UnitDef(
    val id: String,
    val name: String,
    val symbol: String,
    val strategy: ConversionStrategy
)

/**
 * Definition of a conversion category (e.g. Length, Temperature).
 * The [baseUnitId] identifies which unit is the anchor (factor = 1 / identity formula).
 */
data class CategoryDef(
    val id: String,
    val name: String,
    val units: List<UnitDef>,
    val isStringBased: Boolean = false
)

/**
 * Core conversion engine. Converts a value from one unit to another within a category.
 * Dispatches to the correct strategy for each unit, always routing through the base unit.
 */
object ConversionEngine {

    /**
     * Convert [value] from [fromUnitId] to [toUnitId] within [categoryId].
     * Returns a [ConversionResult].
     */
    fun convert(
        categoryId: String,
        fromUnitId: String,
        toUnitId: String,
        value: Double
    ): ConversionResult {
        val category = UnitDefinitions.getCategory(categoryId)
            ?: return ConversionResult.Number(0.0, "—")

        // String-based categories (numeral systems) are handled separately.
        if (category.isStringBased) {
            // Numeral systems: value is already a Long encoded as a double.
            return convertStringBased(category, fromUnitId, toUnitId, value.toLong())
        }

        val fromUnit = category.units.find { it.id == fromUnitId }
            ?: return ConversionResult.Number(0.0, "—")
        val toUnit = category.units.find { it.id == toUnitId }
            ?: return ConversionResult.Number(0.0, "—")

        // Convert input → base unit
        val baseValue = toBase(fromUnit, value)
        // Convert base unit → target
        val result = fromBase(toUnit, baseValue)

        return ConversionResult.Number(result, formatNumber(result))
    }

    /**
     * Convert a string input (used by numeral systems).
     */
    fun convertString(
        categoryId: String,
        fromUnitId: String,
        toUnitId: String,
        input: String
    ): ConversionResult {
        val category = UnitDefinitions.getCategory(categoryId)
            ?: return ConversionResult.Text("—")

        val fromUnit = category.units.find { it.id == fromUnitId }
            ?: return ConversionResult.Text("—")
        val toUnit = category.units.find { it.id == toUnitId }
            ?: return ConversionResult.Text("—")

        val fromRadix = numeralRadix(fromUnit.id)
        val toRadix = numeralRadix(toUnit.id)

        // Parse the input string in the source radix
        val longValue = input.trim()
            .replace(" ", "")
            .toLongOrNull(fromRadix)
            ?: return ConversionResult.Text("Invalid input")

        val output = longValue.toString(toRadix).uppercase()
        return ConversionResult.Text(output)
    }

    /**
     * Convert [value] from [fromUnitId] to every other unit in [categoryId].
     * Returns a map of unitId → formatted result string.
     */
    fun convertToAll(
        categoryId: String,
        fromUnitId: String,
        value: Double
    ): Map<String, String> {
        val category = UnitDefinitions.getCategory(categoryId)
            ?: return emptyMap()

        val fromUnit = category.units.find { it.id == fromUnitId }
            ?: return emptyMap()

        val baseValue = toBase(fromUnit, value)

        return category.units.associate { unit ->
            val result = fromBase(unit, baseValue)
            unit.id to formatNumber(result)
        }
    }

    /**
     * Convert a string input to all units (for numeral systems).
     * Returns a map of unitId → string result.
     */
    fun convertStringToAll(
        categoryId: String,
        fromUnitId: String,
        input: String
    ): Map<String, String> {
        val category = UnitDefinitions.getCategory(categoryId)
            ?: return emptyMap()

        val fromUnit = category.units.find { it.id == fromUnitId }
            ?: return emptyMap()

        val fromRadix = numeralRadix(fromUnit.id)
        val longValue = input.trim().replace(" ", "").toLongOrNull(fromRadix)

        return if (longValue == null) {
            category.units.associate { it.id to "" }
        } else {
            category.units.associate { unit ->
                val toRadix = numeralRadix(unit.id)
                unit.id to longValue.toString(toRadix).uppercase()
            }
        }
    }

    // ── Strategy dispatch ──

    private fun toBase(unit: UnitDef, value: Double): Double = when (unit.strategy) {
        is ConversionStrategy.FactorStrategy -> value * unit.strategy.factor
        is ConversionStrategy.FormulaStrategy -> unit.strategy.toBase(value)
        is ConversionStrategy.ReciprocalStrategy -> {
            if (value == 0.0) 0.0 else unit.strategy.factor / value
        }
    }

    private fun fromBase(unit: UnitDef, baseValue: Double): Double = when (unit.strategy) {
        is ConversionStrategy.FactorStrategy -> baseValue / unit.strategy.factor
        is ConversionStrategy.FormulaStrategy -> unit.strategy.fromBase(baseValue)
        is ConversionStrategy.ReciprocalStrategy -> {
            if (baseValue == 0.0) 0.0 else unit.strategy.factor / baseValue
        }
    }

    private fun convertStringBased(
        category: CategoryDef,
        fromUnitId: String,
        toUnitId: String,
        value: Long
    ): ConversionResult {
        val toUnit = category.units.find { it.id == toUnitId }
            ?: return ConversionResult.Text("—")
        val toRadix = numeralRadix(toUnit.id)
        return ConversionResult.Text(value.toString(toRadix).uppercase())
    }

    private fun numeralRadix(unitId: String): Int = when (unitId) {
        "binary" -> 2
        "octal" -> 8
        "decimal_numeral" -> 10
        "hexadecimal" -> 16
        else -> 10
    }

    // ── Formatting ──

    /**
     * Format a number with significant-figure awareness and clean trailing zeros.
     */
    fun formatNumber(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "—"

        // For very large or very small numbers, use scientific notation
        val absValue = abs(value)
        if (absValue != 0.0 && (absValue < 1e-4 || absValue >= 1e12)) {
            return formatScientific(value)
        }

        // Round to 8 significant figures to remove floating-point noise
        val rounded = roundToSignificantFigures(value, 8)

        // Format with up to 6 decimal places, then strip trailing zeros
        val formatted = if (rounded == rounded.toLong().toDouble()) {
            rounded.toLong().toString()
        } else {
            // Use up to 6 decimal places (Kotlin common — no String.format)
            val withDecimals = formatDecimal(rounded, 6)
            // Strip trailing zeros and trailing decimal point
            withDecimals.trimEnd('0').trimEnd('.')
        }

        return addThousandsSeparator(formatted)
    }

    private fun formatScientific(value: Double): String {
        val exponent = floor(log10(abs(value))).toInt()
        val mantissa = value / 10.0.pow(exponent)
        val mantissaStr = formatDecimal(mantissa, 4).trimEnd('0').trimEnd('.')
        return "${mantissaStr}E${if (exponent >= 0) "+" else ""}$exponent"
    }

    /**
     * Format a Double to [maxDecimalPlaces] decimal places without using String.format
     * (which is not available in Kotlin common).
     */
    private fun formatDecimal(value: Double, maxDecimalPlaces: Int): String {
        val multiplier = 10.0.pow(maxDecimalPlaces)
        val scaled = round(value * multiplier) / multiplier
        // Convert to string — Kotlin Double.toString gives full precision, so we truncate
        val str = scaled.toString()
        val dotIndex = str.indexOf('.')
        if (dotIndex == -1) return "$str.${"0".repeat(maxDecimalPlaces)}"
        val decimalPart = str.substring(dotIndex + 1)
        val padded = decimalPart.padEnd(maxDecimalPlaces, '0')
        return str.substring(0, dotIndex + 1) + padded.take(maxDecimalPlaces)
    }

    /**
     * Round to [sf] significant figures.
     */
    private fun roundToSignificantFigures(value: Double, sf: Int): Double {
        if (value == 0.0) return 0.0
        val d = ceil(log10(abs(value))).toInt()
        val power = sf - d
        val magnitude = 10.0.pow(power)
        val shifted = round(value * magnitude)
        return shifted / magnitude
    }

    /**
     * Add thousands separators (commas) to the integer part of a formatted number string.
     * Handles negative numbers and scientific notation gracefully.
     */
    private fun addThousandsSeparator(formatted: String): String {
        if (formatted.contains('E') || formatted.contains('e')) return formatted

        val negative = formatted.startsWith("-")
        val clean = if (negative) formatted.substring(1) else formatted

        val parts = clean.split(".")
        val intPart = parts[0]
        val decPart = if (parts.size > 1) parts[1] else null

        // Insert commas every 3 digits from the right
        val grouped = buildString {
            val len = intPart.length
            for (i in intPart.indices) {
                if (i > 0 && (len - i) % 3 == 0) {
                    append(',')
                }
                append(intPart[i])
            }
        }

        val result = if (decPart != null) "$grouped.$decPart" else grouped
        return if (negative) "-$result" else result
    }
}
