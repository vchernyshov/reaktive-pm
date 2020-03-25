package dev.garage.rpm.app.common.main.util

actual class PhoneUtil {

    private val countriesMap = HashMap<String, Country>()

    actual fun parsePhone(phone: String): PhoneNumber {

        return PhoneNumber(
            nationalNumber = phone,
            countryCode = ""
        )
    }

    actual fun formatPhoneNumber(country: Country, phoneNumber: String): String {
        return phoneNumber
    }

    actual fun formatPhoneNumber(phoneNumberString: String): String {
        return phoneNumberString
    }

    actual fun isValidPhone(phoneNumber: String): Boolean {
        return true
    }

    actual fun isValidPhone(country: Country, phoneNumber: String): Boolean {
        return true
    }

    actual fun getCountryForCountryCode(code: Int): Country {
        return Country("", code)
    }

    actual fun countries(): List<Country> {
        return countriesMap.values.toList()
    }
}