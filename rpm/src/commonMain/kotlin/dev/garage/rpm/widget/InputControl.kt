package dev.garage.rpm.widget

import com.badoo.reaktive.observable.*
import dev.garage.rpm.*

/**
 * Helps to bind a group of properties of an input field widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the input easier.
 *
 * Instantiate this using the [inputControl] extension function of the presentation model.
 *
 * @see CheckControl
 * @see DialogControl
 */
class InputControl internal constructor(
    initialText: String,
    private val formatter: ((text: String) -> String)?,
    private val hideErrorOnUserInput: Boolean = true
) : PresentationModel() {

    /**
     * The input field text [state][State].
     */
    val text = state(initialText, diffStrategy = null)

    /**
     * The input field error [state][State].
     */
    val error = state<String>(diffStrategy = null)

    /**
     * The input field focus [state][State].
     */
    val focus = state<Boolean>(initialValue = false, diffStrategy = null)

    /**
     * The input field text changes [events][Action].
     */
    val textChanges = action<String>()

    /**
     * The input field focus changes [events][Action].
     */
    val focusChanges = action<Boolean>()

    override fun onCreate() {

        if (formatter != null) {
            textChanges.observable()
                .map { formatter.invoke(it) }
                .subscribe {
                    text.consumer().accept(it)
                    if (hideErrorOnUserInput) error.consumer().accept("")
                }
                .untilDestroy()
        }

        focusChanges.observable()
            .distinctUntilChanged()
            .filter { it != focus.valueOrNull }
            .subscribe(focus)
            .untilDestroy()
    }
}

/**
 * Creates the [InputControl].
 *
 * @param initialText initial text of the input field.
 * @param formatter formats the user input. The default does nothing.
 * @param hideErrorOnUserInput hide the error if user entered something.
 */
fun PresentationModel.inputControl(
    initialText: String = "",
    formatter: ((text: String) -> String)? = { it },
    hideErrorOnUserInput: Boolean = true
): InputControl {
    return InputControl(initialText, formatter, hideErrorOnUserInput).apply {
        attachToParent(this@inputControl)
    }
}