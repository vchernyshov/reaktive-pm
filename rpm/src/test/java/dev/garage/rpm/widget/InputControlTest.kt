package dev.garage.rpm.widget

import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.PresentationModel.Lifecycle.CREATED
import dev.garage.rpm.accept
import dev.garage.rpm.test.PmTestHelper
import org.junit.Before
import org.junit.Test

class InputControlTest {

    private lateinit var presentationModel: PresentationModel
    private lateinit var pmTestHelper: PmTestHelper

    @Before
    fun setUp() {
        presentationModel = object : PresentationModel() {}
        pmTestHelper = PmTestHelper(presentationModel)
    }

    @Test
    fun formatInput() {

        val inputControl = presentationModel.inputControl(
            formatter = { it.toUpperCase() }
        )
        val testObserver = inputControl.text.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)

        inputControl.textChanges.consumer.run {
            accept("a")
            accept("ab")
            accept("abc")
        }

        testObserver.assertValues(
            "", // initial value
            "A",
            "AB",
            "ABC"
        )
        testObserver.assertNotError()
    }

    @Test
    fun notFilterDuplicateValues() {

        val inputControl = presentationModel.inputControl(
            formatter = { it.take(3) }
        )

        val testObserver = inputControl.text.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)

        inputControl.textChanges.consumer.run {
            accept("a")
            accept("ab")
            accept("abc")
            accept("abcd")
        }

        testObserver.assertValues(
            "", // initial value
            "a",
            "ab",
            "abc",
            "abc" // clear user input after formatting because editText contains "abcd"
        )
        testObserver.assertNotError()
    }

    @Test
    fun filterIfFocusNotChanged() {

        val inputControl = presentationModel.inputControl()

        val testObserver = inputControl.focus.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)

        inputControl.focusChanges.consumer.run {
            accept(true)
            accept(true)
            accept(false)
            accept(false)
            accept(true)
            accept(true)
        }

        testObserver.assertValues(
            false, // initial value
            true,
            false,
            true
        )
        testObserver.assertNotError()
    }
}