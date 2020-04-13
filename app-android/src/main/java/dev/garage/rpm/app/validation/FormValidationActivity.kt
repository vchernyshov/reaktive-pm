package dev.garage.rpm.app.validation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.validation.FormValidationPm
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.widget.bindTo

class FormValidationActivity : PmActivity<FormValidationPm>() {

    private lateinit var nameEditLayout: TextInputLayout
    private lateinit var emailEditLayout: TextInputLayout
    private lateinit var phoneEditLayout: TextInputLayout
    private lateinit var passwordEditLayout: TextInputLayout
    private lateinit var confirmPasswordEditLayout: TextInputLayout
    private lateinit var termsCheckbox: MaterialCheckBox
    private lateinit var validateButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        nameEditLayout = findViewById(R.id.nameEditLayout)
        emailEditLayout = findViewById(R.id.emailEditLayout)
        phoneEditLayout = findViewById(R.id.phoneEditLayout)
        passwordEditLayout = findViewById(R.id.passwordEditLayout)
        confirmPasswordEditLayout = findViewById(R.id.confirmPasswordEditLayout)
        termsCheckbox = findViewById(R.id.termsCheckbox)
        validateButton = findViewById(R.id.validateButton)
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