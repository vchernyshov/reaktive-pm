package dev.garage.rpm.delegate

import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.bindTo
import dev.garage.rpm.navigation.NavigationMessageDispatcher
import dev.garage.rpm.navigation.NavigationalPm
import platform.Foundation.NSUUID

/**
 *  Common delegate serves for forwarding the lifecycle[PresentationModel.Lifecycle] directly into the [PresentationModel][PresentationModel].
 *  Can be used to implement your own delegate for the View[PmView].
 */
class CommonDelegate<PM, V>(
    private val pmView: PmView<PM>,
    private val navigationMessagesDispatcher: NavigationMessageDispatcher
)
        where PM : PresentationModel,
              V : PmView<PM> {

    private lateinit var pmTag: String

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("UNCHECKED_CAST")
        PmStore.getPm(pmTag) { pmView.providePresentationModel() } as PM
    }

    fun onCreate() {
        pmTag = NSUUID.UUID().UUIDString()
        if (presentationModel.currentLifecycleState == null) {
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.CREATED)
        }
    }

    fun onBind() {

        val pm = presentationModel

        if (pm.currentLifecycleState == PresentationModel.Lifecycle.CREATED
            || pm.currentLifecycleState == PresentationModel.Lifecycle.UNBINDED
        ) {
            pm.lifecycleConsumer.accept(PresentationModel.Lifecycle.BINDED)
            pmView.onBindPresentationModel(pm)

            if (pm is NavigationalPm) {
                pm.navigationMessages.bindTo {
                    navigationMessagesDispatcher.dispatch(it)
                }
            }
        }
    }

    fun onResume() {
        if (presentationModel.currentLifecycleState == PresentationModel.Lifecycle.BINDED
            || presentationModel.currentLifecycleState == PresentationModel.Lifecycle.PAUSED
        ) {
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.RESUMED)
        }
    }

    fun onPause() {
        if (presentationModel.currentLifecycleState == PresentationModel.Lifecycle.RESUMED) {
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.PAUSED)
        }
    }

    fun onUnbind() {
        if (presentationModel.currentLifecycleState == PresentationModel.Lifecycle.PAUSED
            || presentationModel.currentLifecycleState == PresentationModel.Lifecycle.BINDED
        ) {
            pmView.onUnbindPresentationModel()
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.UNBINDED)
        }
    }

    fun onDestroy() {
        if (presentationModel.currentLifecycleState == PresentationModel.Lifecycle.CREATED
            || presentationModel.currentLifecycleState == PresentationModel.Lifecycle.UNBINDED
        ) {
            PmStore.removePm(pmTag)
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.DESTROYED)
        }
    }
}