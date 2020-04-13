package dev.garage.rpm.app.main.ui.phone

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.merge
import com.google.android.material.textfield.TextInputLayout
import dev.garage.rpm.accept
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.main.util.Country
import dev.garage.rpm.app.common.ui.phone.AuthByPhonePm
import dev.garage.rpm.app.main.extensions.showKeyboard
import dev.garage.rpm.app.main.ui.base.Screen
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.bindings.editorActions
import dev.garage.rpm.widget.bindTo

class AuthByPhoneScreen : Screen<AuthByPhonePm>() {

    private lateinit var editCountryCodeLayout: TextInputLayout
    private lateinit var editPhoneNumberLayout: TextInputLayout
    private lateinit var countryName: TextView
    private lateinit var sendButton: View
    private lateinit var phoneNumberEdit: EditText

    override val screenLayout = R.layout.screen_auth_by_phone

    override fun providePresentationModel(): AuthByPhonePm {
        return AuthByPhonePm(
            App.component.phoneUtil,
            App.component.resources,
            App.component.authModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editCountryCodeLayout = view.findViewById(R.id.editCountryCodeLayout)
        editPhoneNumberLayout = view.findViewById(R.id.editPhoneNumberLayout)
        countryName = view.findViewById(R.id.countryName)
        sendButton = view.findViewById(R.id.sendButton)
        phoneNumberEdit = view.findViewById(R.id.phoneNumberEdit)
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