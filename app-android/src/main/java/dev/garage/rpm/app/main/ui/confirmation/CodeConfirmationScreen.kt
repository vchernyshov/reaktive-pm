package dev.garage.rpm.app.main.ui.confirmation

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.merge
import com.google.android.material.textfield.TextInputLayout
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.ui.confirmation.CodeConfirmationPm
import dev.garage.rpm.app.main.extensions.showKeyboard
import dev.garage.rpm.app.main.ui.base.Screen
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.bindings.editorActions
import dev.garage.rpm.bindings.navigationClicks
import dev.garage.rpm.widget.bindTo

class CodeConfirmationScreen : Screen<CodeConfirmationPm>() {

    private lateinit var codeEditLayout: TextInputLayout
    private lateinit var sendButton: View
    private lateinit var toolbar: Toolbar
    private lateinit var codeEdit: EditText

    companion object {
        private const val ARG_PHONE = "arg_phone"
        fun newInstance(phone: String) = CodeConfirmationScreen().apply {
            arguments = Bundle().apply {
                putString(ARG_PHONE, phone)
            }
        }
    }

    override val screenLayout = R.layout.screen_code_confirmation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeEditLayout = view.findViewById(R.id.codeEditLayout)
        sendButton = view.findViewById(R.id.sendButton)
        toolbar = view.findViewById(R.id.toolbar)
        codeEdit = view.findViewById(R.id.codeEdit)
    }

    override fun providePresentationModel(): CodeConfirmationPm {
        return CodeConfirmationPm(
            arguments!!.getString(ARG_PHONE)!!,
            App.component.resources,
            App.component.authModel
        )
    }

    override fun onBindPresentationModel(pm: CodeConfirmationPm) {
        super.onBindPresentationModel(pm)

        pm.code.bindTo(codeEditLayout)
        pm.inProgress.bindTo(progressConsumer)
        pm.sendButtonEnabled.bindTo(sendButton::setEnabled)

        toolbar.navigationClicks().bindTo(pm.backAction)

        merge(
            sendButton.clicks(),
            codeEdit.editorActions()
                .filter { it == EditorInfo.IME_ACTION_SEND }
                .map { Unit }
        ).bindTo(pm.sendClicks)
    }

    override fun onResume() {
        super.onResume()
        codeEdit.showKeyboard()
    }
}
