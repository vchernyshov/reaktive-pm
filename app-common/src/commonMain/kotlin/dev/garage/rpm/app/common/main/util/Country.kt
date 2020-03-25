package dev.garage.rpm.app.common.main.util

class Country(val region: String, val countryCallingCode: Int) {

    val name = getCountryName(region)

    companion object {
        private const val UNKNOWN_REGION = "ZZ"
        private const val INVALID_COUNTRY_CODE = 0
        val UNKNOWN = Country(
            UNKNOWN_REGION,
            INVALID_COUNTRY_CODE
        )
    }

}

expect fun getCountryName(region: String): String