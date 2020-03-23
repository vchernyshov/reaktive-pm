package dev.garage.rpm.util

import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.scheduler.trampolineScheduler
import com.badoo.reaktive.test.scheduler.TestScheduler
import org.junit.rules.ExternalResource

class SchedulersRule(private val useTestScheduler: Boolean = false) : ExternalResource() {

    private lateinit var _testScheduler: TestScheduler

    val testScheduler: TestScheduler
        get() {
            check(useTestScheduler) { "TestScheduler is switched off." }
            return _testScheduler
        }

    override fun before() {
        val computationScheduler = if (useTestScheduler) {
            _testScheduler = TestScheduler()
            _testScheduler
        } else {
            trampolineScheduler
        }

        overrideSchedulers(
            io = { trampolineScheduler },
            main = { trampolineScheduler },
            computation = { computationScheduler }
        )
    }

    override fun after() {
        overrideSchedulers()
    }
}