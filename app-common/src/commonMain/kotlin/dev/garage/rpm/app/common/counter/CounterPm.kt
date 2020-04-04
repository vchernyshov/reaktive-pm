package dev.garage.rpm.app.common.counter

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import dev.garage.rpm.*

class CounterPm : PresentationModel() {

    companion object {
        const val MAX_COUNT = 10
    }

    private val _count = state(initialValue = 0)
    val count = state(stateSource = { _count.observable.map { it.toString() } })

    val minusButtonEnabled = state {
        _count.observable.map { it > 0 }
    }

    val plusButtonEnabled = state {
        _count.observable.map { it < MAX_COUNT }
    }

    val minusButtonClicks = action<Unit> {
        this.filter { _count.value > 0 }
            .map { _count.value - 1 }
            .doOnBeforeNext(_count.consumer()::accept)
    }

    val plusButtonClicks = action<Unit> {
        this.filter { _count.value < MAX_COUNT }
            .map { _count.value + 1 }
            .doOnBeforeNext(_count.consumer()::accept)
    }
}