package com.tolongtukar.app.i18n

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Supported UI languages. `code` is persisted in SettingsStorage.
 */
enum class Lang(val code: String) {
    EN("en"),
    MS("ms");

    companion object {
        fun fromCode(code: String): Lang = entries.find { it.code == code } ?: EN
    }
}

/**
 * All user-facing UI strings for one language.
 * Unit names & symbols stay in the international/technical form (kept in
 * UnitDefinitions) — only chrome + category names are localized here.
 */
data class AppStrings(
    val lang: Lang,
    // Chrome
    val appName: String,
    val settings: String,
    val back: String,
    val done: String,
    val reorderCategories: String,
    val reorderUnits: String,
    val insertValue: String,
    val noUnits: String,
    val lastUpdated: String,
    // Settings screen
    val appearance: String,
    val followSystem: String,
    val followSystemDesc: String,
    val darkMode: String,
    val language: String,
    val languageDesc: String,
    val english: String,
    val malay: String,
    val premium: String,
    val premiumActive: String,
    val premiumActiveDesc: String,
    val removeAdsTitle: String,
    val removeAdsDesc: String,
    val removeAdsButton: String,
    val purchaseFailed: String,
    val adsRemovedTitle: String,
    val adsRemovedBody: String,
    val ok: String,
    val great: String,
    val about: String,
    val version: String,
    val terms: String,
    val privacy: String,
    // Category display names, keyed by category id
    val categoryNames: Map<String, String>
)

private val EN = AppStrings(
    lang = Lang.EN,
    appName = "TolongTukar",
    settings = "Settings",
    back = "Back",
    done = "Done",
    reorderCategories = "Reorder categories",
    reorderUnits = "Reorder units",
    insertValue = "Insert value",
    noUnits = "No units available",
    lastUpdated = "Last updated:",
    appearance = "Appearance",
    followSystem = "Follow System Theme",
    followSystemDesc = "Match device light/dark setting",
    darkMode = "Dark Mode",
    language = "Language",
    languageDesc = "Choose the app language",
    english = "English",
    malay = "Bahasa Melayu",
    premium = "Premium",
    premiumActive = "Premium Active",
    premiumActiveDesc = "Ads removed forever. Thank you! 💙",
    removeAdsTitle = "Remove Ads Forever",
    removeAdsDesc = "Enjoy TolongTukar without any advertisements.\nOne-time purchase. No subscriptions.",
    removeAdsButton = "Remove Ads — RM 4.99",
    purchaseFailed = "Purchase Failed",
    adsRemovedTitle = "Ads Removed! 🎉",
    adsRemovedBody = "Thank you for your purchase! TolongTukar is now ad-free forever.",
    ok = "OK",
    great = "Great!",
    about = "About",
    version = "Version",
    terms = "Terms & Conditions",
    privacy = "Privacy Policy",
    categoryNames = mapOf(
        "length" to "Length",
        "area" to "Area",
        "volume" to "Volume",
        "mass" to "Mass",
        "time" to "Time",
        "speed" to "Speed",
        "force" to "Force",
        "fuel_consumption" to "Fuel Consumption",
        "pressure" to "Pressure",
        "energy" to "Energy",
        "power" to "Power",
        "angle" to "Angle",
        "torque" to "Torque",
        "digital_data" to "Digital Data",
        "si_prefixes" to "SI Prefixes",
        "density" to "Density",
        "temperature" to "Temperature",
        "numeral_systems" to "Numeral Systems",
        "shoe_size" to "Shoe Size",
        "currency" to "Currency"
    )
)

private val MS = AppStrings(
    lang = Lang.MS,
    appName = "TolongTukar",
    settings = "Tetapan",
    back = "Kembali",
    done = "Selesai",
    reorderCategories = "Susun semula kategori",
    reorderUnits = "Susun semula unit",
    insertValue = "Masukkan nilai",
    noUnits = "Tiada unit tersedia",
    lastUpdated = "Kemas kini terakhir:",
    appearance = "Paparan",
    followSystem = "Ikut Tema Sistem",
    followSystemDesc = "Ikut tetapan terang/gelap peranti",
    darkMode = "Mod Gelap",
    language = "Bahasa",
    languageDesc = "Pilih bahasa aplikasi",
    english = "English",
    malay = "Bahasa Melayu",
    premium = "Premium",
    premiumActive = "Premium Aktif",
    premiumActiveDesc = "Iklan dibuang selamanya. Terima kasih! 💙",
    removeAdsTitle = "Buang Iklan Selamanya",
    removeAdsDesc = "Nikmati TolongTukar tanpa sebarang iklan.\nBayaran sekali sahaja. Tiada langganan.",
    removeAdsButton = "Buang Iklan — RM 4.99",
    purchaseFailed = "Pembelian Gagal",
    adsRemovedTitle = "Iklan Dibuang! 🎉",
    adsRemovedBody = "Terima kasih atas pembelian anda! TolongTukar kini bebas iklan selamanya.",
    ok = "OK",
    great = "Bagus!",
    about = "Perihal",
    version = "Versi",
    terms = "Terma & Syarat",
    privacy = "Dasar Privasi",
    categoryNames = mapOf(
        "length" to "Panjang",
        "area" to "Luas",
        "volume" to "Isipadu",
        "mass" to "Jisim",
        "time" to "Masa",
        "speed" to "Kelajuan",
        "force" to "Daya",
        "fuel_consumption" to "Penggunaan Bahan Api",
        "pressure" to "Tekanan",
        "energy" to "Tenaga",
        "power" to "Kuasa",
        "angle" to "Sudut",
        "torque" to "Tork",
        "digital_data" to "Data Digital",
        "si_prefixes" to "Awalan SI",
        "density" to "Ketumpatan",
        "temperature" to "Suhu",
        "numeral_systems" to "Sistem Angka",
        "shoe_size" to "Saiz Kasut",
        "currency" to "Mata Wang"
    )
)

object I18n {
    fun strings(lang: Lang): AppStrings = when (lang) {
        Lang.EN -> EN
        Lang.MS -> MS
    }

    /** Localized category display name, falling back to the English default. */
    fun categoryName(strings: AppStrings, categoryId: String, fallback: String): String =
        strings.categoryNames[categoryId] ?: fallback
}

/** Ambient current strings — provided at the App root. */
val LocalStrings = staticCompositionLocalOf { EN }
