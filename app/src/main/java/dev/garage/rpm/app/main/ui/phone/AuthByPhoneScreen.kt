package dev.garage.rpm.app.main.ui.phone

import android.view.inputmethod.EditorInfo
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.merge
import dev.garage.rpm.accept
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.main.extensions.showKeyboard
import dev.garage.rpm.app.main.ui.base.Screen
import dev.garage.rpm.app.main.util.Country
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.bindings.editorActions
import dev.garage.rpm.widget.bindTo
import kotlinx.android.synthetic.main.screen_auth_by_phone.*

class AuthByPhoneScreen : Screen<AuthByPhonePm>() {

    override val screenLayout = R.layout.screen_auth_by_phone

    override fun providePresentationModel(): AuthByPhonePm {
        return AuthByPhonePm(
            App.component.phoneUtil,
            App.component.resourceProvider,
            App.component.authModel
        )
    }

    override fun onBindPresentationModel(pm: AuthByPhonePm) {
        super.onBindPresentationModel(pm)

        pm.countryCode.bindTo(editCountryCodeLayout)
        pm.phoneNumber.bindTo(editPhoneNumberLayout)
        pm.chosenCountry.bindTo { countryName.text = it.name }

        pm.inProgress.bindTo(progressConsumer)
        pm.sendButtonEnabled.bindTo(sendButton::setEnabled)

        countryName.clicks().bindTo(pm.countryClicks)


        merge(
            sendButton.clicks(),
            phoneNumberEdit.editorActions()
                .filter { it == EditorInfo.IME_ACTION_SEND }
                .map { Unit }
        ).bindTo(pm.sendClicks)
    }

    fun onCountryChosen(country: Country) {
        presentationModel.chooseCountry.consumer.accept(country)
    }

    override fun onResume() {
        super.onResume()
        phoneNumberEdit.showKeyboard()
    }
}