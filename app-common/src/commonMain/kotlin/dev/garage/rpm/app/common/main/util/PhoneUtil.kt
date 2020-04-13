package dev.garage.rpm.app.common.main.util

const val MAX_PHONE_LENGTH = 17

fun onlyPhone(phoneNumberString: String): String {
    return "+${phoneNumberString.onlyDigits()}"
}

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")

class NumberParseException(message: String) : Exception(message)

interface PhoneUtil {

    fun parsePhone(phone: String): PhoneNumber

    fun formatPhoneNumber(country: Country, phoneNumber: String): String

    fun formatPhoneNumber(phoneNumberString: String): String

    fun isValidPhone(phoneNumber: String): Boolean

    fun isValidPhone(country: Country, phoneNumber: String): Boolean

    fun getCountryForCountryCode(code: Int): Country

    fun countries(): List<Country>
}