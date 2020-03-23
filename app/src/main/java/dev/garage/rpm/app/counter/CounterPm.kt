package dev.garage.rpm.app.counter

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.state

class CounterPm : PresentationModel() {

    companion object {
        const val MAX_COUNT = 10
    }

    val count = state(initialValue = 0)

    val minusButtonEnabled = state {
        count.observable.map { it > 0 }
    }

    val plusButtonEnabled = state {
        count.observable.map { it < MAX_COUNT }
    }

    val minusButtonClicks = action<Unit> {
        this.filter { count.value > 0 }
            .map { count.value - 1 }
            .doOnBeforeNext(count.consumer::accept)
    }

    val plusButtonClicks = action<Unit> {
        this.filter { count.value < MAX_COUNT }
            .map { count.value + 1 }
            .doOnBeforeNext(count.consumer::accept)
    }
}