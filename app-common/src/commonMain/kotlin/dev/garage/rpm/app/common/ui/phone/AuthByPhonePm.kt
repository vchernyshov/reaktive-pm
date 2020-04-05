package dev.garage.rpm.app.common.ui.phone

import com.badoo.reaktive.completable.asObservable
import com.badoo.reaktive.completable.doOnBeforeComplete
import com.badoo.reaktive.completable.doOnBeforeError
import com.badoo.reaktive.observable.*
import dev.garage.rpm.*
import dev.garage.rpm.app.common.main.AppNavigationMessage
import dev.garage.rpm.app.common.main.AppNavigationMessage.PhoneSentSuccessfully
import dev.garage.rpm.app.common.main.model.AuthModel
import dev.garage.rpm.app.common.main.util.*
import dev.garage.rpm.app.common.ui.base.ScreenPresentationModel
import dev.garage.rpm.validation.empty
import dev.garage.rpm.validation.formValidator
import dev.garage.rpm.validation.input
import dev.garage.rpm.validation.valid
import dev.garage.rpm.widget.inputControl

class AuthByPhonePm(
    private val phoneUtil: PhoneUtil,
    private val resources: Resources,
    private val authModel: AuthModel
) : ScreenPresentationModel() {

    val phoneNumber = inputControl(formatter = null)
    val countryCode = inputControl(
        initialText = "+7",
        formatter = {
            val code = "+${it.onlyDigits().take(5)}"
            if (code.length > 5) {
                try {
                    val number = phoneUtil.parsePhone(code)
                    phoneNumber.focus.accept(true)
                    phoneNumber.textChanges.accept(number.nationalNumber)
                    "+${number.countryCode}"
                } catch (e: NumberParseException) {
                    code
                }
            } else {
                code
            }
        }
    )
    val chosenCountry = state<Country> {
        countryCode.text.observable
            .map {
                val code = it.onlyDigits()
                if (code.isNotEmpty()) {
                    phoneUtil.getCountryForCountryCode(code.onlyDigits().toInt())
                } else {
                    Country.UNKNOWN
                }
            }
    }

    val inProgress = state(false)

    val sendButtonEnabled = state(false) {
        combineLatest(
            phoneNumber.textChanges.observable(),
            chosenCountry.observable
        ) { number: String, country: Country ->
            phoneUtil.isValidPhone(country, number)
        }
    }

    val countryClicks = action<Unit> {
        this.map { AppNavigationMessage.ChooseCountry }
            .doOnBeforeNext(navigationMessages.consumer()::accept)
    }

    val chooseCountry = action<Country> {
        this.doOnBeforeNext {
            countryCode.textChanges.accept("+${it.countryCallingCode}")
            chosenCountry.accept(it)
            phoneNumber.focus.accept(true)
        }
    }

    val sendClicks = action<Unit> {
        this.skipWhileInProgress(inProgress)
            .filter { formValidator.validate() }
            .map { "${countryCode.text.value} ${phoneNumber.text.value}" }
            .switchMapCompletable { phone ->
                authModel.sendPhone(phone)
                    .bindProgress(inProgress)
                    .doOnBeforeComplete { sendMessage(PhoneSentSuccessfully(phone)) }
                    .doOnBeforeError(errorConsumer::invoke)
            }.asObservable<Any>()
    }

    override fun onCreate() {
        super.onCreate()

        combineLatest(
            phoneNumber.textChanges.observable(),
            chosenCountry.observable
        ) { number: String, country: Country ->
            phoneUtil.formatPhoneNumber(country, number)
        }
            .subscribe(phoneNumber.text)
            .untilDestroy()
    }

    private val formValidator = formValidator {
        input(phoneNumber) {
            empty(resources.enterPhoneNumber)
            valid(
                validation = { phoneNumber ->
                    phoneUtil.isValidPhone(chosenCountry.value, phoneNumber)
                },
                errorMessage = resources.invalidPhoneNumber
            )
        }
    }
}