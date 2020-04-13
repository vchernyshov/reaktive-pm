package dev.garage.rpm.app.common.ui.base

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.command
import dev.garage.rpm.navigation.NavigationMessage
import dev.garage.rpm.navigation.NavigationalPm
import dev.garage.rpm.app.common.main.AppNavigationMessage.Back
import dev.garage.rpm.widget.dialogControl

abstract class ScreenPresentationModel : PresentationModel(), NavigationalPm {

    override val navigationMessages = command<NavigationMessage>()

    val errorDialog = dialogControl<String, Unit>()

    protected val errorConsumer: ((Throwable?) -> Unit) = {
        errorDialog.show(it?.message ?: "Unknown error")
    }

    open val backAction = action<Unit> {
        this.map { Back }
            .doOnBeforeNext { navigationMessages.consumer().accept(it) }
    }

    protected fun sendMessage(message: NavigationMessage) {
        navigationMessages.accept(message)
    }

    protected fun showError(errorMessage: String?) {
        errorDialog.show(errorMessage ?: "Unknown error")
    }
}