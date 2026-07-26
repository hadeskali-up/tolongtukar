package com.tolongtukar.app.converter

import kotlin.math.PI

/**
 * All conversion categories and their units, with conversion factors / formulas
 * relative to the category's base unit.
 */
object UnitDefinitions {

    // ── Helpers ──
    private fun factor(id: String, name: String, symbol: String, f: Double) =
        UnitDef(id, name, symbol, ConversionStrategy.FactorStrategy(f))

    private fun formula(
        id: String, name: String, symbol: String,
        toBase: (Double) -> Double,
        fromBase: (Double) -> Double
    ) = UnitDef(id, name, symbol, ConversionStrategy.FormulaStrategy(toBase, fromBase))

    private fun reciprocal(id: String, name: String, symbol: String, f: Double) =
        UnitDef(id, name, symbol, ConversionStrategy.ReciprocalStrategy(f))

    // ═══════════════════════════════════════════════════════════════════════
    //  CATEGORY DEFINITIONS
    // ═══════════════════════════════════════════════════════════════════════

    val categories: List<CategoryDef> = listOf(
        // ── 1. LENGTH (base: meters) ──
        CategoryDef("length", "Length", listOf(
            factor("meters", "Meters", "m", 1.0),
            factor("feet", "Feet", "ft", 0.3048),
            factor("yards", "Yards", "yd", 0.9144),
            factor("kilometers", "Kilometers", "km", 1000.0),
            factor("miles", "Miles", "mi", 1609.344),
            factor("nautical_miles", "Nautical Miles", "nmi", 1852.0),
            factor("centimeters", "Centimeters", "cm", 0.01),
            factor("inches", "Inches", "in", 0.0254),
            factor("mils", "Mils", "mil", 0.0000254),
            factor("millimeters", "Millimeters", "mm", 0.001),
            factor("micrometers", "Micrometers", "µm", 1e-6),
            factor("nanometers", "Nanometers", "nm", 1e-9),
            factor("angstroms", "Angstroms", "Å", 1e-10),
            factor("picometers", "Picometers", "pm", 1e-12),
            factor("feet_us", "Feet (US Survey)", "ft (US)", 0.3048006),
            factor("astronomical_units", "Astronomical Units", "AU", 1.495978707e11),
            factor("light_years", "Light Years", "ly", 9.4607304725808e15),
            factor("parsec", "Parsec", "pc", 3.0856775814913673e16)
        )),

        // ── 2. AREA (base: square meters) ──
        CategoryDef("area", "Area", listOf(
            factor("square_meters", "Square Meters", "m²", 1.0),
            factor("square_feet", "Square Feet", "ft²", 0.092903),
            factor("square_yard", "Square Yard", "yd²", 0.836127),
            factor("hectares", "Hectares", "ha", 10000.0),
            factor("acres", "Acres", "ac", 4046.86),
            factor("square_kilometers", "Square Kilometers", "km²", 1e6),
            factor("square_miles", "Square Miles", "mi²", 2589988.110336),
            factor("square_centimeters", "Square Centimeters", "cm²", 0.0001),
            factor("square_millimeters", "Square Millimeters", "mm²", 1e-6),
            factor("square_inches", "Square Inches", "in²", 0.00064516),
            factor("are", "Are", "a", 100.0),
            factor("square_feet_us", "Square Feet (US Survey)", "ft² (US)", 0.092903411613474)
        )),

        // ── 3. VOLUME (base: liters) ──
        CategoryDef("volume", "Volume", listOf(
            factor("cubic_meters", "Cubic Meters", "m³", 1000.0),
            factor("liters", "Liters", "L", 1.0),
            factor("us_gallons", "US Gallons", "gal (US)", 3.78541),
            factor("imperial_gallons", "Imperial Gallons", "gal (UK)", 4.54609),
            factor("us_pints", "US Pints", "pt (US)", 0.473176),
            factor("imperial_pints", "Imperial Pints", "pt (UK)", 0.568261),
            factor("us_quarts", "US Quarts", "qt (US)", 0.946353),
            factor("deciliters", "Deciliters", "dL", 0.1),
            factor("centiliters", "Centiliters", "cL", 0.01),
            factor("milliliters", "Milliliters", "mL", 0.001),
            factor("microliters", "Microliters", "µL", 1e-6),
            factor("tablespoons_us", "Tablespoons (US)", "tbsp", 0.0147868),
            factor("australian_tablespoons", "Australian Tablespoons", "tbsp (AU)", 0.02),
            factor("teaspoons_us", "Teaspoons (US)", "tsp", 0.00492892),
            factor("teaspoons_metric", "Teaspoons (Metric)", "tsp (met)", 0.005),
            factor("cups", "Cups", "cup", 0.236588),
            factor("cubic_millimeters", "Cubic Millimeters", "mm³", 1e-6),
            factor("cubic_centimeters", "Cubic Centimeters", "cm³", 0.001),
            factor("cubic_inches", "Cubic Inches", "in³", 0.0163871),
            factor("cubic_feet", "Cubic Feet", "ft³", 28.3168),
            factor("us_fluid_ounces", "US Fluid Ounces", "fl oz (US)", 0.0295735),
            factor("imperial_fluid_ounces", "Imperial Fluid Ounces", "fl oz (UK)", 0.0284131),
            factor("us_gill", "US Gill", "gi (US)", 0.118294),
            factor("imperial_gill", "Imperial Gill", "gi (UK)", 0.142065)
        )),

        // ── 4. MASS (base: kilograms) ──
        CategoryDef("mass", "Mass", listOf(
            factor("kilograms", "Kilograms", "kg", 1.0),
            factor("pounds", "Pounds", "lb", 0.453592),
            factor("ounces", "Ounces", "oz", 0.0283495),
            factor("tonnes", "Tonnes", "t", 1000.0),
            factor("grams", "Grams", "g", 0.001),
            factor("ettograms", "Ettograms", "hg", 0.1),
            factor("centigrams", "Centigrams", "cg", 1e-5),
            factor("milligrams", "Milligrams", "mg", 1e-6),
            factor("carats", "Carats", "ct", 0.0002),
            factor("quintals", "Quintals", "q", 100.0),
            factor("pennyweights", "Pennyweights", "dwt", 0.00155517),
            factor("troy_ounces", "Troy Ounces", "oz t", 0.0311035),
            factor("uma", "Unified Atomic Mass", "u", 1.66053906660e-27),
            factor("stones", "Stones", "st", 6.35029)
        )),

        // ── 5. TIME (base: seconds) ──
        CategoryDef("time", "Time", listOf(
            factor("seconds", "Seconds", "s", 1.0),
            factor("minutes_time", "Minutes", "min", 60.0),
            factor("hours", "Hours", "h", 3600.0),
            factor("days", "Days", "d", 86400.0),
            factor("weeks", "Weeks", "wk", 604800.0),
            factor("years_365", "Years (365 days)", "yr", 31536000.0),
            factor("lustrum", "Lustrum (5 yr)", "5yr", 157680000.0),
            factor("decades", "Decades", "dec", 315360000.0),
            factor("centuries", "Centuries", "c", 3153600000.0),
            factor("millennium", "Millennium", "ka", 31536000000.0),
            factor("deciseconds", "Deciseconds", "ds", 0.1),
            factor("centiseconds", "Centiseconds", "cs", 0.01),
            factor("milliseconds", "Milliseconds", "ms", 0.001),
            factor("microseconds", "Microseconds", "µs", 1e-6),
            factor("nanoseconds", "Nanoseconds", "ns", 1e-9)
        )),

        // ── 6. SPEED (base: m/s) ──
        // Pace units (min/km, min/mi) are reciprocal: base = factor / value
        CategoryDef("speed", "Speed", listOf(
            factor("kilometers_per_hour", "Kilometers per Hour", "km/h", 0.277778),
            factor("miles_per_hour", "Miles per Hour", "mph", 0.44704),
            factor("meters_per_second", "Meters per Second", "m/s", 1.0),
            factor("feets_per_second", "Feet per Second", "ft/s", 0.3048),
            factor("knots", "Knots", "kn", 0.514444),
            reciprocal("minutes_per_kilometer", "Minutes per Kilometer", "min/km", 60.0),
            reciprocal("minutes_per_mile", "Minutes per Mile", "min/mi", 1609.344 / 60.0 * 60.0),
            factor("speed_of_light", "Speed of Light", "c", 299792458.0)
        )),

        // ── 7. FORCE (base: newton) ──
        CategoryDef("force", "Force", listOf(
            factor("newton", "Newton", "N", 1.0),
            factor("kilogram_force", "Kilogram-force", "kgf", 9.80665),
            factor("pound_force", "Pound-force", "lbf", 4.44822),
            factor("dyne", "Dyne", "dyn", 1e-5),
            factor("poundal", "Poundal", "pdl", 0.138255)
        )),

        // ── 8. FUEL CONSUMPTION (base: km/liter) ──
        // litersPer100km and milesPerGallon are reciprocal
        CategoryDef("fuel_consumption", "Fuel Consumption", listOf(
            factor("kilometers_per_liter", "Kilometers per Liter", "km/L", 1.0),
            reciprocal("liters_per_100km", "Liters per 100 km", "L/100km", 100.0),
            reciprocal("miles_per_us_gallon", "Miles per US Gallon", "mpg (US)", 0.425144 * 100.0),
            reciprocal("miles_per_imperial_gallon", "Miles per Imperial Gallon", "mpg (UK)", 0.354006 * 100.0)
        )),

        // ── 9. PRESSURE (base: pascal) ──
        CategoryDef("pressure", "Pressure", listOf(
            factor("atmosphere", "Atmosphere", "atm", 101325.0),
            factor("bar", "Bar", "bar", 100000.0),
            factor("millibar", "Millibar", "mbar", 100.0),
            factor("psi", "Pound per Square Inch", "psi", 6894.76),
            factor("pascal", "Pascal", "Pa", 1.0),
            factor("kilo_pascal", "Kilopascal", "kPa", 1000.0),
            factor("torr", "Torr", "Torr", 133.322),
            factor("inch_of_mercury", "Inch of Mercury", "inHg", 3386.39),
            factor("hecto_pascal", "Hectopascal", "hPa", 100.0),
            factor("ksi", "Kilopound per Square Inch", "ksi", 6894757.2932),
            factor("mega_pascal", "Megapascal", "MPa", 1000000.0),
            factor("giga_pascal", "Gigapascal", "GPa", 1000000000.0)
        )),

        // ── 10. ENERGY (base: joule) ──
        CategoryDef("energy", "Energy", listOf(
            factor("kilowatt_hours", "Kilowatt-hours", "kWh", 3600000.0),
            factor("watt_hours", "Watt-hours", "Wh", 3600.0),
            factor("kilocalories", "Kilocalories", "kcal", 4184.0),
            factor("calories", "Calories", "cal", 4.184),
            factor("joules", "Joules", "J", 1.0),
            factor("kilojoules", "Kilojoules", "kJ", 1000.0),
            factor("electronvolts", "Electronvolts", "eV", 1.602176565e-19),
            factor("energy_foot_pound", "Foot-pound", "ft·lbf", 1.35582),
            factor("british_thermal_unit", "British Thermal Unit", "BTU", 1055.06)
        )),

        // ── 11. POWER (base: watt) ──
        CategoryDef("power", "Power", listOf(
            factor("kilowatt", "Kilowatt", "kW", 1000.0),
            factor("european_horse_power", "Metric Horsepower", "hp (met)", 735.49875),
            factor("imperial_horse_power", "Imperial Horsepower", "hp (imp)", 745.7),
            factor("watt", "Watt", "W", 1.0),
            factor("megawatt", "Megawatt", "MW", 1000000.0),
            factor("gigawatt", "Gigawatt", "GW", 1000000000.0),
            factor("milliwatt", "Milliwatt", "mW", 0.001)
        )),

        // ── 12. ANGLE (base: degree) ──
        CategoryDef("angle", "Angle", listOf(
            factor("degree", "Degree", "°", 1.0),
            factor("radians", "Radians", "rad", 57.295779513),
            factor("minutes_angle", "Minutes", "'", 0.0166667),
            factor("seconds_angle", "Seconds", "\"", 0.000277778)
        )),

        // ── 13. TORQUE (base: newton-meter) ──
        CategoryDef("torque", "Torque", listOf(
            factor("newton_meter", "Newton-meter", "N·m", 1.0),
            factor("kilogram_force_meter", "Kilogram-force meter", "kgf·m", 9.80665),
            factor("dyne_meter", "Dyne-meter", "dyn·m", 1e-5),
            factor("pound_force_feet", "Pound-force feet", "lbf·ft", 1.35582),
            factor("pound_force_inch", "Pound-force inch", "lbf·in", 0.112984829),
            factor("poundal_meter", "Poundal meter", "pdl·m", 0.138255)
        )),

        // ── 14. DIGITAL DATA (base: bit) ──
        CategoryDef("digital_data", "Digital Data", listOf(
            factor("byte", "Byte", "B", 8.0),
            factor("bit", "Bit", "b", 1.0),
            factor("nibble", "Nibble", "nib", 4.0),
            factor("kilobyte", "Kilobyte (10³)", "KB", 8000.0),
            factor("megabyte", "Megabyte (10⁶)", "MB", 8000000.0),
            factor("gigabyte", "Gigabyte (10⁹)", "GB", 8e9),
            factor("terabyte", "Terabyte (10¹²)", "TB", 8e12),
            factor("petabyte", "Petabyte (10¹⁵)", "PB", 8e15),
            factor("exabyte", "Exabyte (10¹⁸)", "EB", 8e18),
            factor("kibibyte", "Kibibyte (2¹⁰)", "KiB", 8192.0),
            factor("mebibyte", "Mebibyte (2²⁰)", "MiB", 8388608.0),
            factor("gibibyte", "Gibibyte (2³⁰)", "GiB", 8589934592.0),
            factor("tebibyte", "Tebibyte (2⁴⁰)", "TiB", 8796093022208.0),
            factor("pebibyte", "Pebibyte (2⁵⁰)", "PiB", 9007199254740992.0),
            factor("exbibyte", "Exbibyte (2⁶⁰)", "EiB", 9223372036854775808.0),
            factor("kilobit", "Kilobit (10³)", "kb", 1000.0),
            factor("megabit", "Megabit (10⁶)", "Mb", 1e6),
            factor("gigabit", "Gigabit (10⁹)", "Gb", 1e9),
            factor("terabit", "Terabit (10¹²)", "Tb", 1e12),
            factor("petabit", "Petabit (10¹⁵)", "Pb", 1e15),
            factor("exabit", "Exabit (10¹⁸)", "Eb", 1e18),
            factor("kibibit", "Kibibit (2¹⁰)", "Kib", 1024.0),
            factor("mebibit", "Mebibit (2²⁰)", "Mib", 1048576.0),
            factor("gibibit", "Gibibit (2³⁰)", "Gib", 1073741824.0),
            factor("tebibit", "Tebibit (2⁴⁰)", "Tib", 1099511627776.0),
            factor("pebibit", "Pebibit (2⁵⁰)", "Pib", 1125899906842624.0),
            factor("exbibit", "Exbibit (2⁶⁰)", "Eib", 1152921504606846976.0)
        )),

        // ── 15. SI PREFIXES (base: base unit) ──
        CategoryDef("si_prefixes", "SI Prefixes", listOf(
            factor("base", "Base (1)", "", 1.0),
            factor("deca", "Deca (10)", "da", 10.0),
            factor("hecto", "Hecto (10²)", "h", 100.0),
            factor("kilo", "Kilo (10³)", "k", 1000.0),
            factor("mega", "Mega (10⁶)", "M", 1e6),
            factor("giga", "Giga (10⁹)", "G", 1e9),
            factor("tera", "Tera (10¹²)", "T", 1e12),
            factor("peta", "Peta (10¹⁵)", "P", 1e15),
            factor("exa", "Exa (10¹⁸)", "E", 1e18),
            factor("zetta", "Zetta (10²¹)", "Z", 1e21),
            factor("yotta", "Yotta (10²⁴)", "Y", 1e24),
            factor("deci", "Deci (10⁻¹)", "d", 0.1),
            factor("centi", "Centi (10⁻²)", "c", 0.01),
            factor("milli", "Milli (10⁻³)", "m", 0.001),
            factor("micro", "Micro (10⁻⁶)", "µ", 1e-6),
            factor("nano", "Nano (10⁻⁹)", "n", 1e-9),
            factor("pico", "Pico (10⁻¹²)", "p", 1e-12),
            factor("femto", "Femto (10⁻¹⁵)", "f", 1e-15),
            factor("atto", "Atto (10⁻¹⁸)", "a", 1e-18),
            factor("zepto", "Zepto (10⁻²¹)", "z", 1e-21),
            factor("yocto", "Yocto (10⁻²⁴)", "y", 1e-24)
        )),

        // ── 16. DENSITY (base: g/L) ──
        CategoryDef("density", "Density", listOf(
            factor("grams_per_liter", "Grams per Liter", "g/L", 1.0),
            factor("grams_per_cubic_centimeter", "Grams per Cubic Centimeter", "g/cm³", 1000.0),
            factor("grams_per_milliliter", "Grams per Milliliter", "g/mL", 1000.0),
            factor("grams_per_deciliter", "Grams per Deciliter", "g/dL", 10.0),
            factor("kilograms_per_liter", "Kilograms per Liter", "kg/L", 1000.0),
            factor("kilograms_per_cubic_meter", "Kilograms per Cubic Meter", "kg/m³", 1.0),
            factor("milligrams_per_liter", "Milligrams per Liter", "mg/L", 0.001),
            factor("milligrams_per_deciliter", "Milligrams per Deciliter", "mg/dL", 0.01),
            factor("milligrams_per_milliliter", "Milligrams per Milliliter", "mg/mL", 1.0),
            factor("milligrams_per_cubic_meter", "Milligrams per Cubic Meter", "mg/m³", 1e-6),
            factor("milligrams_per_cubic_centimeter", "Milligrams per Cubic Centimeter", "mg/cm³", 1.0),
            factor("micrograms_per_liter", "Micrograms per Liter", "µg/L", 1e-6),
            factor("micrograms_per_deciliter", "Micrograms per Deciliter", "µg/dL", 1e-5),
            factor("micrograms_per_milliliter", "Micrograms per Milliliter", "µg/mL", 0.001),
            factor("pounds_per_cubic_inches", "Pounds per Cubic Inch", "lb/in³", 17218.03),
            factor("pounds_per_cubic_feet", "Pounds per Cubic Foot", "lb/ft³", 0.453592 * 35.3147)
        )),

        // ── 17. TEMPERATURE (base: celsius) — FORMULA ──
        CategoryDef("temperature", "Temperature", listOf(
            formula("celsius", "Celsius", "°C",
                toBase = { it },
                fromBase = { it }
            ),
            formula("fahrenheit", "Fahrenheit", "°F",
                toBase = { (it - 32.0) * 5.0 / 9.0 },
                fromBase = { it * 9.0 / 5.0 + 32.0 }
            ),
            formula("kelvin", "Kelvin", "K",
                toBase = { it - 273.15 },
                fromBase = { it + 273.15 }
            ),
            formula("reamur", "Réaumur", "°Re",
                toBase = { it * 5.0 / 4.0 },
                fromBase = { it * 4.0 / 5.0 }
            ),
            formula("romer", "Rømer", "°Rø",
                toBase = { (it - 7.5) * 40.0 / 21.0 },
                fromBase = { it * 21.0 / 40.0 + 7.5 }
            ),
            formula("delisle", "Delisle", "°De",
                toBase = { (100.0 - it) * 2.0 / 3.0 },
                fromBase = { (100.0 - it) * 3.0 / 2.0 }
            ),
            formula("rankine", "Rankine", "°Ra",
                toBase = { (it - 491.67) * 5.0 / 9.0 },
                fromBase = { (it + 273.15) * 9.0 / 5.0 }
            )
        )),

        // ── 18. NUMERAL SYSTEMS — STRING BASED ──
        CategoryDef("numeral_systems", "Numeral Systems", listOf(
            UnitDef("binary", "Binary", "base 2", ConversionStrategy.FactorStrategy(1.0)),
            UnitDef("octal", "Octal", "base 8", ConversionStrategy.FactorStrategy(1.0)),
            UnitDef("decimal_numeral", "Decimal", "base 10", ConversionStrategy.FactorStrategy(1.0)),
            UnitDef("hexadecimal", "Hexadecimal", "base 16", ConversionStrategy.FactorStrategy(1.0))
        ), isStringBased = true),

        // ── 19. SHOE SIZE (base: cm) — FORMULA ──
        CategoryDef("shoe_size", "Shoe Size", listOf(
            formula("centimeters", "Centimeters", "cm",
                toBase = { it },
                fromBase = { it }
            ),
            formula("inches_shoe", "Inches", "in",
                toBase = { it * 2.54 },
                fromBase = { it / 2.54 }
            ),
            formula("eu_china", "EU / China", "EU",
                toBase = { (it - 1.2) / 1.5 },
                fromBase = { it * 1.5 + 1.2 }
            ),
            formula("usa_canada_man", "US/Canada Men", "US M",
                toBase = { (it + 11.67) / 0.762 },
                fromBase = { it * 0.762 - 11.67 }
            ),
            formula("usa_canada_woman", "US/Canada Women", "US W",
                toBase = { (it + 10.67) / 0.762 },
                fromBase = { it * 0.762 - 10.67 }
            ),
            formula("usa_canada_child", "US/Canada Child", "US C",
                toBase = { (it + 11.67) / 0.762 },
                fromBase = { it * 0.762 - 11.67 }
            ),
            formula("uk_india_man", "UK/India Men", "UK M",
                toBase = { (it + 10.5) / 0.762 },
                fromBase = { it * 0.762 - 10.5 }
            ),
            formula("uk_india_woman", "UK/India Women", "UK W",
                toBase = { (it + 9.5) / 0.762 },
                fromBase = { it * 0.762 - 9.5 }
            ),
            formula("uk_india_child", "UK/India Child", "UK C",
                toBase = { (it + 11.67) / 0.762 },
                fromBase = { it * 0.762 - 11.67 }
            ),
            formula("japan", "Japan", "JP",
                toBase = { it },
                fromBase = { it }
            )
        )),

        // ── 20. CURRENCY ──
        CategoryDef("currency", "Currency", CurrencyConverter.currencyUnits())
    )

    // ── Lookup helpers ──

    fun getCategory(id: String): CategoryDef? = categories.find { it.id == id }

    fun getCategoryByName(name: String): CategoryDef? =
        categories.find { it.name.equals(name, ignoreCase = true) }

    val categoryNames: List<String> get() = categories.map { it.name }
}
