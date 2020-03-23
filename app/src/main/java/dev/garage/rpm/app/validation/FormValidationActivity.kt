package dev.garage.rpm.app.validation

import android.os.Bundle
import android.widget.Toast
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.widget.bindTo
import kotlinx.android.synthetic.main.activity_form.*

class FormValidationActivity : PmActivity<FormValidationPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
    }

    override fun providePresentationModel(): FormValidationPm {
        return FormValidationPm(App.component.phoneUtil)
    }

    override fun onBindPresentationModel(pm: FormValidationPm) {
        pm.name.bindTo(nameEditLayout)
        pm.email.bindTo(emailEditLayout)
        pm.phone.bindTo(phoneEditLayout)
        pm.password.bindTo(passwordEditLayout)
        pm.confirmPassword.bindTo(confirmPasswordEditLayout)
        pm.termsCheckBox.bindTo(termsCheckbox)

        pm.acceptTermsOfUse.bindTo {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        validateButton.clicks().bindTo(pm.validateButtonClicks)
    }
}