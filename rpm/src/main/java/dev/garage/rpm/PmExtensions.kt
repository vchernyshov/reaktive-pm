package dev.garage.rpm

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.doOnBeforeFinally
import com.badoo.reaktive.completable.doOnBeforeSubscribe
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.doOnBeforeFinally
import com.badoo.reaktive.maybe.doOnBeforeSubscribe
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeFinally
import com.badoo.reaktive.single.doOnBeforeSubscribe
import dev.garage.rpm.util.bufferSingleValueWhileIdle
import dev.garage.rpm.util.bufferValuesWhileIdle

/**
 * Convenience to bind the [progress][progressConsumer] to the [Single].
 */
fun <T> Single<T>.bindProgress(progressConsumer: Consumer<Boolean>): Single<T> {
    return this
        .doOnBeforeSubscribe { progressConsumer.accept(true) }
        .doOnBeforeFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Maybe].
 */
fun <T> Maybe<T>.bindProgress(progressConsumer: Consumer<Boolean>): Maybe<T> {
    return this
        .doOnBeforeSubscribe { progressConsumer.accept(true) }
        .doOnBeforeFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Completable].
 */
fun Completable.bindProgress(progressConsumer: Consumer<Boolean>): Completable {
    return this
        .doOnBeforeSubscribe { progressConsumer.accept(true) }
        .doOnBeforeFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][state] to the [Single].
 */
fun <T> Single<T>.bindProgress(state: State<Boolean>): Single<T> {
    return this.bindProgress(state.relay)
}

/**
 * Convenience to bind the [progress][state] to the [Maybe].
 */
fun <T> Maybe<T>.bindProgress(state: State<Boolean>): Maybe<T> {
    return this.bindProgress(state.relay)
}

/**
 * Convenience to bind the [progress][state] to the [Completable].
 */
fun Completable.bindProgress(state: State<Boolean>): Completable {
    return this.bindProgress(state.relay)
}

/**
 * Convenience to filter out items emitted by the source [Observable] when in progress ([progressState] last value is `true`).
 */
fun <T> Observable<T>.skipWhileInProgress(progressState: Observable<Boolean>): Observable<T> {
    return this
        .withLatestFrom(progressState.startWith(false.toObservable())) { t: T, inProgress: Boolean ->
            Pair(t, inProgress)
        }
        .filter { (_, inProgress) -> !inProgress }
        .map { (item, _) -> item }
}

/**
 * Convenience to filter out items emitted by the source [Observable] when in progress ([state] last value is `true`).
 */
fun <T> Observable<T>.skipWhileInProgress(state: State<Boolean>): Observable<T> {
    return this.skipWhileInProgress(state.observable)
}

/**
 * Returns the [Observable] that emits items when active, and buffers them when [idle][isIdle].
 * Buffered items is emitted when idle state ends.
 * @param isIdle shows when the idle state begins (`true`) and ends (`false`).
 * @param bufferSize number of items the buffer can hold. `null` means not constrained.
 */
internal fun <T> Observable<T>.bufferWhileIdle(
    isIdle: Observable<Boolean>,
    bufferSize: Int? = null
): Observable<T> {
    return this.observeOn(mainScheduler)
        .let {
            if (bufferSize == 1) {
                it.bufferSingleValueWhileIdle(isIdle)
            } else {
                it.bufferValuesWhileIdle(isIdle, bufferSize)
            }
        }
}
