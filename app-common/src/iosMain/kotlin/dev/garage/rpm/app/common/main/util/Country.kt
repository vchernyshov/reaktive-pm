package dev.garage.rpm.app.common.main.util

import platform.Foundation.NSLocale
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.localizedStringForCountryCode

actual fun getCountryName(region: String): String =
    NSLocale.localeWithLocaleIdentifier("EN").localizedStringForCountryCode(region) ?: region