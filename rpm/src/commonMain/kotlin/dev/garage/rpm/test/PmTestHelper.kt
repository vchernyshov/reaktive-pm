package dev.garage.rpm.test

import dev.garage.rpm.PresentationModel
import dev.garage.rpm.PresentationModel.Lifecycle
import dev.garage.rpm.accept

/**
 * Helps to test [PresentationModel].
 *
 * @param pm presentation model under test.
 */
class PmTestHelper(val pm: PresentationModel) {

    enum class LifecycleSteps { ALL, BYPASS_BINDING, BYPASS_RESUMING }

    /**
     * Sets the lifecycle of the [presentation model][pm] under test to the specified [state][lifecycleState].
     * This will also create natural sequence of states before the requested one.
     *
     * **Note** that because of it's nature [Command][Command] emits items right away
     * only in [RESUMED] lifecycle state. So if you want to test it, be sure to set the state.
     *
     * @param lifecycleState lifecycle state to set to.
     * @param lifecycleSteps lifecycle path.
     * Sometimes when testing you may need a shorter lifecycle path: bypassing [resuming][LifecycleSteps.BYPASS_BINDING] or [binding][LifecycleSteps.BYPASS_RESUMING].
     * By default it is [all steps][LifecycleSteps.ALL]
     *
     * @throws IllegalStateException if requested state is not acceptable considering the current state.
     */
    fun setLifecycleTo(
        lifecycleState: Lifecycle,
        lifecycleSteps: LifecycleSteps = LifecycleSteps.ALL
    ) {

        checkStateAllowed(lifecycleState)

        when {
            isResumedAgain(lifecycleState) -> pm.lifecycleConsumer.accept(Lifecycle.RESUMED)
            isBindedAgain(lifecycleState) -> pm.lifecycleConsumer.accept(Lifecycle.BINDED)
            else -> {

                val currentLifecycleState = pm.currentLifecycleState

                Lifecycle.values()
                    .filter {
                        if (currentLifecycleState != null) {
                            it > currentLifecycleState
                        } else {
                            true
                        }
                    }
                    .filter { it <= lifecycleState }
                    .filter {
                        when (lifecycleSteps) {
                            LifecycleSteps.BYPASS_RESUMING -> {
                                if (lifecycleState > Lifecycle.PAUSED) {
                                    it < Lifecycle.RESUMED || it > Lifecycle.PAUSED
                                } else {
                                    true
                                }
                            }
                            LifecycleSteps.BYPASS_BINDING -> {
                                if (lifecycleState > Lifecycle.UNBINDED) {
                                    it < Lifecycle.BINDED || it > Lifecycle.UNBINDED
                                } else {
                                    true
                                }
                            }
                            LifecycleSteps.ALL -> true
                        }
                    }
                    .forEach {
                        pm.lifecycleConsumer.accept(it)
                    }
            }
        }
    }

    private fun checkStateAllowed(lifecycleState: Lifecycle) {
        pm.currentLifecycleState?.let { currentState ->
            check(
                !(lifecycleState <= currentState
                        && !isBindedAgain(lifecycleState)
                        && !isResumedAgain((lifecycleState)))
            ) { "You can't set lifecycle state as $lifecycleState when it already is $pm.currentLifecycleState." }
        }
    }

    private fun isResumedAgain(lifecycleState: Lifecycle): Boolean {
        return pm.currentLifecycleState == Lifecycle.PAUSED && lifecycleState == Lifecycle.RESUMED
    }

    private fun isBindedAgain(lifecycleState: Lifecycle): Boolean {
        return pm.currentLifecycleState == Lifecycle.UNBINDED && lifecycleState == Lifecycle.BINDED
    }
}