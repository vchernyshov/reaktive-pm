package dev.garage.rpm.app.common.main.util

import com.google.i18n.phonenumbers.*
import java.util.*

actual class PhoneUtil {

    private val countriesMap = HashMap<String, Country>()
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    init {
        for (region in phoneNumberUtil.supportedRegions) {
            val country = Country(
                region,
                phoneNumberUtil.getCountryCodeForRegion(region)
            )
            countriesMap[region] = country
        }
    }

    @Throws(NumberParseException::class)
    actual fun parsePhone(phone: String): PhoneNumber {
        return phoneNumberUtil.parse(phone, PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY).let {
            PhoneNumber(
                "${it.nationalNumber}",
                "${it.countryCode}"
            )
        }
    }

    actual fun formatPhoneNumber(country: Country, phoneNumber: String): String {

        if (country === Country.UNKNOWN) return phoneNumber.onlyDigits()

        val code = "+${country.countryCallingCode}"
        var formattedPhone = code + phoneNumber.onlyDigits()

        val asYouTypeFormatter = phoneNumberUtil.getAsYouTypeFormatter(country.region)

        for (ch in (code + phoneNumber.onlyDigits()).toCharArray()) {
            formattedPhone = asYouTypeFormatter.inputDigit(ch)
        }

        return formattedPhone.replace(code, "").trim()
    }

    actual fun formatPhoneNumber(phoneNumberString: String): String {

        val phoneNumber = onlyPhone(
            phoneNumberString
        ).take(MAX_PHONE_LENGTH)
        var formattedPhone: String = phoneNumber

        val asYouTypeFormatter =
            phoneNumberUtil.getAsYouTypeFormatter(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY)

        for (ch in phoneNumber.toCharArray()) {
            formattedPhone = asYouTypeFormatter.inputDigit(ch)
        }

        return formattedPhone.trim()
    }

    actual fun isValidPhone(phoneNumber: String): Boolean {
        return try {
            phoneNumberUtil.isValidNumber(
                phoneNumberUtil.parse(
                    onlyPhone(
                        phoneNumber
                    ), null
                )
            )
        } catch (e: Exception) {
            false
        }
    }

    actual fun isValidPhone(country: Country, phoneNumber: String): Boolean {
        return try {
            val number = Phonenumber.PhoneNumber().apply {
                countryCode = country.countryCallingCode
                nationalNumber = phoneNumber.onlyDigits().toLong()
            }
            phoneNumberUtil.isValidNumberForRegion(number, country.region)
        } catch (e: NumberFormatException) {
            false
        }
    }

    actual fun getCountryForCountryCode(code: Int): Country {
        return countriesMap[phoneNumberUtil.getRegionCodeForCountryCode(code)] ?: Country.UNKNOWN
    }

    actual fun countries(): List<Country> {
        return countriesMap.values.toList()
    }
}