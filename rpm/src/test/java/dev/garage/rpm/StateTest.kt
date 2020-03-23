package dev.garage.rpm

import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.nhaarman.mockitokotlin2.spy
import dev.garage.rpm.PresentationModel.Lifecycle.*
import dev.garage.rpm.test.PmTestHelper
import dev.garage.rpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StateTest {

    @get:Rule
    val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var pmTestHelper: PmTestHelper

    @Before
    fun setUp() {
        pm = spy()
        pmTestHelper = PmTestHelper(pm)
    }

    @Test
    fun defaultDiffStrategyTheSameContent() {

        val state = pm.state<String>()
        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")

        testObserver.assertValues("foo")
    }

    @Test
    fun defaultDiffStrategyContentIsDifferent() {

        val state = pm.state<String>()
        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("bar")

        testObserver.assertValues("foo", "bar")
    }


    @Test
    fun withoutDiffStrategy() {

        val state = pm.state<String>(diffStrategy = null)
        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")

        testObserver.assertValues("foo", "foo")
    }

    @Test
    fun customDiffStrategy() {

        val state = pm.state(diffStrategy = object : DiffStrategy<String> {

            override fun areTheSame(new: String, old: String): Boolean {
                return if (new == "foo") {
                    false
                } else {
                    new == old
                }
            }

            override fun computeAsync() = true

        })

        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")
        state.relay.accept("bar")
        state.relay.accept("bar")
        state.relay.accept("baz")
        state.relay.accept("baz")

        testObserver.assertValues("foo", "foo", "bar", "baz")
    }

    @Test
    fun blocksUpdatesBeforeCreated() {
        val state = pm.state("foo")
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        testObserver.assertNoValues()

    }

    @Test
    fun blocksUpdatesBeforeBinded() {
        val state = pm.state("foo")
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(CREATED)

        testObserver.assertNoValues()
    }

    @Test
    fun blocksUpdatesBeforeResumed() {
        val state = pm.state("foo")
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(BINDED)

        testObserver.assertNoValues()
    }

    @Test
    fun blocksUpdatesAfterPaused() {
        val state = pm.state<String>()
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(PAUSED)

        state.relay.accept("foo")

        testObserver.assertNoValues()

    }

    @Test
    fun updatesWhenResumed() {
        val state = pm.state<String>()
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(RESUMED)

        state.relay.accept("foo")
        state.relay.accept("bar")

        testObserver.assertValues("foo", "bar")
    }

    @Test
    fun sendBufferedValueAfterResumed() {
        val state = pm.state<String>()
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        state.relay.accept("foo")
        state.relay.accept("bar")

        pmTestHelper.setLifecycleTo(RESUMED)

        testObserver.assertValues("bar")
    }

    @Test
    fun sendBufferedValueAfterResumedAgain() {
        val state = pm.state<String>()
        val relay = PublishSubject<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(PAUSED)

        state.relay.accept("foo")
        state.relay.accept("bar")

        pmTestHelper.setLifecycleTo(RESUMED)

        testObserver.assertValues("bar")

    }
}