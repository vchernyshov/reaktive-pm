package dev.garage.rpm.app.common.ui.confirmation

import com.badoo.reaktive.completable.asObservable
import com.badoo.reaktive.completable.doOnBeforeComplete
import com.badoo.reaktive.completable.doOnBeforeError
import com.badoo.reaktive.observable.*
import dev.garage.rpm.action
import dev.garage.rpm.app.common.ui.base.ScreenPresentationModel
import dev.garage.rpm.app.common.main.AppNavigationMessage.PhoneConfirmed
import dev.garage.rpm.app.common.main.model.AuthModel
import dev.garage.rpm.app.common.main.util.Resources
import dev.garage.rpm.app.common.main.util.onlyDigits
import dev.garage.rpm.bindProgress
import dev.garage.rpm.skipWhileInProgress
import dev.garage.rpm.state
import dev.garage.rpm.validation.empty
import dev.garage.rpm.validation.formValidator
import dev.garage.rpm.validation.input
import dev.garage.rpm.validation.minSymbols
import dev.garage.rpm.widget.inputControl

class CodeConfirmationPm(
    private val phone: String,
    private val resources: Resources,
    private val authModel: AuthModel
) : ScreenPresentationModel() {

    companion object {
        private const val CODE_LENGTH = 4
    }

    val code = inputControl(
        formatter = { it.onlyDigits().take(CODE_LENGTH) }
    )
    val inProgress = state(false)

    val sendButtonEnabled = state(false) {
        code.text.observable.map { it.length == CODE_LENGTH }
    }

    private val codeFilled = code.text.observable
        .filter { it.length == CODE_LENGTH }
        .distinctUntilChanged()
        .map { Unit }

    val sendClicks = action<Unit> {
        merge(this, codeFilled)
            .skipWhileInProgress(inProgress)
            .map { code.text.value }
            .filter { formValidator.validate() }
            .switchMapCompletable { code ->
                authModel.sendConfirmationCode(phone, code)
                    .bindProgress(inProgress)
                    .doOnBeforeComplete { sendMessage(PhoneConfirmed) }
                    .doOnBeforeError { errorConsumer.invoke(it) }
            }.asObservable<Any>()
    }

    private val formValidator = formValidator {
        input(code) {
            empty(resources.enterConfirmationCode)
            minSymbols(CODE_LENGTH, resources.invalidConfirmationCode)
        }
    }
}