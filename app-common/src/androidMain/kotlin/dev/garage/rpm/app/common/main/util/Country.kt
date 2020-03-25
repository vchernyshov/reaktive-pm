package dev.garage.rpm.app.common.main.util

import java.util.*

actual fun getCountryName(region: String): String =
    Locale("en", region).getDisplayCountry(Locale.ENGLISH)