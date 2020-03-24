package dev.garage.rpm

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.delay
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.delay
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.behavior.BehaviorSubject

val <T> Subject<T>.consumer: Consumer<T>
    get() = this

fun <T> Consumer<T>.accept(value: T) {
    onNext(value)
}

fun <T> BehaviorSubject<T>.hasValue(): Boolean = this.value != null

fun <T> Observable<T>.subscribe(consumer: Consumer<T>): Disposable = this.subscribe {
    consumer.onNext(it)
}

fun <T> Single<T>.subscribe(consumer: Consumer<T>): Disposable = this.subscribe {
    consumer.onNext(it)
}

fun <T> Maybe<T>.subscribe(consumer: Consumer<T>): Disposable = this.subscribe {
    consumer.onNext(it)
}

fun <T> Observable<T>.startWithArray(vararg values: T): Observable<T>  {
    val observables = mutableListOf<Observable<T>>()
    values.forEach {
        observables.add(it.toObservable())
    }
    observables.add(this)
    return observables.concat()
}

fun <T: Any?> Observable<T?>.notNull(): Observable<T> =
    observableUnsafe { observer ->
        subscribe(
            object : ObservableObserver<T?>, Observer by observer, CompletableCallbacks by observer {
                override fun onNext(value: T?) {
                    if (value != null) {
                        observer.onNext(value)
                    }
                }
            }
        )
    }


fun Completable.delay(delayMillis: Long, delayError: Boolean = false): Completable =
    this.delay(delayMillis, computationScheduler, delayError)

fun <T> Single<T>.delay(delayMillis: Long, delayError: Boolean = false): Single<T> =
    this.delay(delayMillis, computationScheduler, delayError)

fun <T> Observable<T>.delay(delayMillis: Long, delayError: Boolean = false): Observable<T> =
    this.delay(delayMillis, computationScheduler, delayError)
