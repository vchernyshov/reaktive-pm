package dev.garage.rpm

import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.nhaarman.mockitokotlin2.spy
import dev.garage.rpm.PresentationModel.Lifecycle
import dev.garage.rpm.PresentationModel.Lifecycle.*
import dev.garage.rpm.navigation.NavigationMessage
import dev.garage.rpm.navigation.NavigationalPm
import dev.garage.rpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFailsWith

class ChildPresentationModelTest {

    @get:Rule
    val schedulers = SchedulersRule()

    private class ScreenPm : PresentationModel(), NavigationalPm {
        override val navigationMessages = command<NavigationMessage>()
    }

    private lateinit var pm: ScreenPm
    private lateinit var childPm: ScreenPm
    private lateinit var lifecycleObserver: TestObservableObserver<Lifecycle>

    @Before
    fun setUp() {
        pm = spy()
        childPm = spy()
        lifecycleObserver = childPm.lifecycleObservable.test()
    }

    @Test
    fun attachToParent() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test
    fun detachFromParent() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        childPm.detachFromParent()

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test
    fun attachToParentAfterCreated() {
        pm.lifecycleConsumer.accept(CREATED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValues(CREATED)
    }

    @Test
    fun attachToParentAfterBinded() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValues(CREATED, BINDED)
    }

    @Test
    fun attachToParentAfterResumed() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED)
    }

    @Test
    fun attachToParentAfterPaused() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValues(CREATED, BINDED)
    }

    @Test
    fun attachToParentAfterUnbinded() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValues(CREATED)
    }

    @Test
    fun throwOnAttachToParentAfterDestroyed() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        assertFailsWith<IllegalStateException> {
            childPm.attachToParent(pm)
        }
    }

    @Test
    fun throwOnAttachToItself() {
        assertFailsWith<IllegalArgumentException> {
            childPm.attachToParent(childPm)
        }
    }

    @Test
    fun throwOnChildPmReuse() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        childPm.detachFromParent()

        assertFailsWith<IllegalStateException> {
            childPm.attachToParent(pm)
        }
    }

    @Test
    fun passNavigationMessagesToParent() {
        val testMessage = object : NavigationMessage {}
        val testObserver = pm.navigationMessages.observable.test()
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)

        childPm.navigationMessages.relay.accept(testMessage)

        testObserver.assertValues(testMessage)
    }

}