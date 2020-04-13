package dev.garage.rpm.widget

import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.PresentationModel.Lifecycle.CREATED
import dev.garage.rpm.accept
import dev.garage.rpm.test.PmTestHelper
import org.junit.Test

class CheckControlTest {

    @Test
    fun filterIfValueNotChanged() {
        val pm = object : PresentationModel() {}
        val pmTestHelper = PmTestHelper(pm)

        val checkbox = pm.checkControl()

        pmTestHelper.setLifecycleTo(CREATED)

        val testObserver = checkbox.checked.observable.test()

        checkbox.checkedChanges.consumer.run {
            accept(true)
            accept(true)
            accept(false)
            accept(false)
            accept(true)
            accept(false)
        }

        testObserver.assertValues(
            false, // initial value
            true,
            false,
            true,
            false
        )
        testObserver.assertNotError()
    }
}