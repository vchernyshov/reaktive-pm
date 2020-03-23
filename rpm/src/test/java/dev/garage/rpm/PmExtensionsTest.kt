package dev.garage.rpm

import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.completableOfError
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeOfError
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.single.singleOfError
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.assertSuccess
import dev.garage.rpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PmExtensionsTest {

    @get:Rule
    val schedulers = SchedulersRule()

    private lateinit var progress: BehaviorSubject<Boolean>

    private lateinit var isIdleObservable: BehaviorSubject<Boolean>
    private lateinit var relay: PublishSubject<Int>

    @Before
    fun setUp() {
        progress = BehaviorSubject(false)

        relay = PublishSubject()
        isIdleObservable = BehaviorSubject(false)
    }

    @Test
    fun bindProgressSingle() {
        val testObserver = TestSingleObserver<Int>()
        val progressObserver = progress.test()

        singleOf(1)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertSuccess(1)
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun bindProgressOnErrorSingle() {
        val error = IllegalArgumentException()
        val testObserver = TestSingleObserver<Int>()
        val progressObserver = progress.test()

        singleOfError<Int>(error)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertError(error)
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun bindProgressMaybe() {
        val testObserver = TestMaybeObserver<Int>()
        val progressObserver = progress.test()

        maybeOf(1)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertSuccess(1)
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun bindProgressOnErrorMaybe() {
        val error = IllegalArgumentException()
        val testObserver = TestMaybeObserver<Int>()
        val progressObserver = progress.test()

        maybeOfError<Int>(error)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertError(error)
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun bindProgressOnEmptyMaybe() {
        val testObserver = TestMaybeObserver<Int>()
        val progressObserver = progress.test()

        maybeOfEmpty<Int>()
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertNotSuccess()
        testObserver.assertComplete()
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun bindProgressCompletable() {
        val testObserver = TestCompletableObserver()
        val progressObserver = progress.test()

        completableOfEmpty()
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertComplete()
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun bindProgressOnErrorCompletable() {
        val error = IllegalArgumentException()
        val testObserver = TestCompletableObserver()
        val progressObserver = progress.test()

        completableOfError(error)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertError(error)
        progressObserver.assertValues(false, true, false)
    }

    @Test
    fun skipWhileInProgress() {
        val testObserver = relay.skipWhileInProgress(progress).test()

        relay.accept(1)
        relay.accept(2)
        progress.accept(true)
        relay.accept(3)
        relay.accept(4)
        progress.accept(false)
        relay.accept(5)
        relay.accept(6)

        testObserver.assertValues(1, 2, 5, 6)
    }

    @Test
    fun bufferWhileIdleReceiveItems() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)

        testObserver.assertValues(1, 2)
    }

    @Test fun bufferWhileIdleBlockItemsWhenIdle() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)

        testObserver.assertValues(1, 2)
    }

    @Test
    fun bufferWhileIdlePassItemsAfterIdle() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValues(1, 2, 3, 4)
    }

    @Test fun bufferWhileIdleRestrictBufferedItemsCount() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable, bufferSize = 1).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValues(1, 2, 4)
    }

    @Test
    fun bufferWhileIdleCanStartIdlingWhenConsumingValue() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable)
            .doOnBeforeNext { value ->
                // Use already consumed value to open buffer
                if (value == 2) isIdleObservable.accept(true)
            }
            .test()

        relay.accept(1)
        relay.accept(2)
        // Here idling will begin
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValues(1, 2, 3, 4)
    }

    @Test
    fun bufferWhileIdleOpensOneBufferAtATime() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValues(1, 2, 3, 4)
    }

    @Test
    fun bufferWhileIdleNoReactionOnMultipleCloses() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)
        isIdleObservable.accept(false)

        testObserver.assertValues(1, 2, 3, 4)
    }

    @Test
    fun bufferWhileIdleWithObservable() {
        val testObserver = observableOf(1).bufferWhileIdle(isIdleObservable).test()

        testObserver.assertValues(1)
    }
}