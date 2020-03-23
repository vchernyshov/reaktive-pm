package dev.garage.rpm.widget

import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.test
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.assertValueAt
import dev.garage.rpm.assertValuesCount
import dev.garage.rpm.widget.DialogControl.Display.Absent
import dev.garage.rpm.widget.DialogControl.Display.Displayed
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class DialogControlTest {

    private lateinit var dialogControl: DialogControl<Unit, Unit>

    @Before
    fun setUp() {
        dialogControl = createDialogControl()
    }

    private fun createDialogControl(): DialogControl<Unit, Unit> {
        val pm = object : PresentationModel() {}
        return pm.dialogControl()
    }

    @Test
    fun displayedOnShow() {
        dialogControl.showForResult(Unit).subscribe()
        assertTrue { dialogControl.displayed.value is Displayed<*> }
    }

    @Test
    fun removedOnResult() {
        dialogControl.showForResult(Unit).subscribe()
        dialogControl.sendResult(Unit)
        assertTrue { dialogControl.displayed.value === Absent }
    }

    @Test
    fun acceptOneResult() {
        val testObserver = dialogControl.showForResult(Unit).test()

        // When two results sent
        dialogControl.sendResult(Unit)
        dialogControl.sendResult(Unit)

        // Then only one is here
        testObserver.assertSuccess(Unit)
    }

    @Test
    fun removedOnDismiss() {
        dialogControl.showForResult(Unit).subscribe()
        dialogControl.dismiss()
        assertTrue { dialogControl.displayed.value === Absent }
    }

    @Test
    fun cancelDialog() {
        val testObserver = dialogControl.showForResult(Unit).test()
        dialogControl.dismiss()

        testObserver.assertSubscribed()
        testObserver.assertNotSuccess()
        testObserver.assertNotError()
        testObserver.assertComplete()
    }

    @Test
    fun dismissPreviousOnNewShow() {
        val displayedObserver = dialogControl.displayed.observable.test()

        val firstObserver = dialogControl.showForResult(Unit).test()
        val secondObserver = dialogControl.showForResult(Unit).test()

        displayedObserver.assertSubscribed()
        displayedObserver.assertValuesCount(4)
        displayedObserver.assertValueAt(0) { it == Absent }
        displayedObserver.assertValueAt(1) { it is Displayed<*> }
        displayedObserver.assertValueAt(2) { it == Absent }
        displayedObserver.assertValueAt(3) { it is Displayed<*> }
        displayedObserver.assertNotError()

        firstObserver.assertSubscribed()
        firstObserver.assertNotSuccess()
        firstObserver.assertNotError()
        firstObserver.assertComplete()

        secondObserver.assertNotSuccess()
    }
}