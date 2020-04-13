package dev.garage.rpm.app.common.ui.main

import com.badoo.reaktive.completable.asObservable
import com.badoo.reaktive.completable.doOnBeforeComplete
import com.badoo.reaktive.completable.doOnBeforeError
import com.badoo.reaktive.maybe.filter
import com.badoo.reaktive.observable.switchMapCompletable
import com.badoo.reaktive.observable.switchMapMaybe
import dev.garage.rpm.action
import dev.garage.rpm.app.common.main.AppNavigationMessage.LogoutCompleted
import dev.garage.rpm.app.common.main.model.AuthModel
import dev.garage.rpm.app.common.ui.base.ScreenPresentationModel
import dev.garage.rpm.bindProgress
import dev.garage.rpm.skipWhileInProgress
import dev.garage.rpm.state
import dev.garage.rpm.widget.dialogControl

class MainPm(private val authModel: AuthModel) : ScreenPresentationModel() {

    sealed class DialogResult {
        object Ok : DialogResult()
        object Cancel : DialogResult()
    }

    val logoutDialog = dialogControl<Unit, DialogResult>()
    val inProgress = state(false)

    val logoutClicks = action<Unit> {
        this.skipWhileInProgress(inProgress)
            .switchMapMaybe {
                logoutDialog.showForResult(Unit)
                    .filter { it == DialogResult.Ok }
            }
            .switchMapCompletable {
                authModel.logout()
                    .bindProgress(inProgress)
                    .doOnBeforeError { showError(it.message) }
                    .doOnBeforeComplete { sendMessage(LogoutCompleted) }
            }.asObservable<Any>()
    }
}