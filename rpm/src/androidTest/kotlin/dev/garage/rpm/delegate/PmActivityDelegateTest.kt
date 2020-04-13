package dev.garage.rpm.delegate

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.nhaarman.mockitokotlin2.*
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.PresentationModel.Lifecycle.*
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.delegate.PmActivityDelegate.RetainMode
import dev.garage.rpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PmActivityDelegateTest {

    @get:Rule
    val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: PmActivity<PresentationModel>
    private lateinit var delegate: PmActivityDelegate<PresentationModel, PmActivity<PresentationModel>>

    @Before
    fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        view = mockView()

        delegate = PmActivityDelegate(view, RetainMode.IS_FINISHING)
    }

    private fun mockView(): PmActivity<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test
    fun callViewMethods() {
        delegate.onCreate(null)
        delegate.onPostCreate()

        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)
        verify(view).onBindPresentationModel(pm)

        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()

        delegate.onDestroy()
        verify(view).onUnbindPresentationModel()

    }

    @Test
    fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onPostCreate()
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        whenever(view.isFinishing).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValues(
            CREATED,
            BINDED,
            RESUMED,
            PAUSED,
            UNBINDED,
            DESTROYED
        )
    }
}