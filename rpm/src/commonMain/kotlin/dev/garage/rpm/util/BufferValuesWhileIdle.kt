package dev.garage.rpm.util

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.handleReaktiveError
import dev.garage.rpm.util.queue.Queue
import dev.garage.rpm.util.queue.SharedQueue

internal fun <T> Observable<T>.bufferValuesWhileIdle(
    isIdleObservable: Observable<Boolean>,
    bufferSize: Int? = null
): Observable<T> =
    observable { emitter->

        val compositeDisposable = CompositeDisposable()
        emitter.setDisposable(compositeDisposable)

        val done = AtomicBoolean(false)

        val isIdle = AtomicBoolean(false)
        val bufferedValues : Queue<T> = SharedQueue()

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable += disposable
                    compositeDisposable += isIdleObservable.subscribe {
                        if (it) {
                            isIdle.value = true
                        } else {
                            isIdle.value = false
                            bufferedValues.forEach { v ->
                                onNext(v)
                            }
                            bufferedValues.clear()
                        }
                    }
                }

                override fun onNext(value: T) {
                    if (done.value) {
                        return
                    }

                    if (isIdle.value) {

                        if (bufferedValues.size == bufferSize) {
                            bufferedValues.poll()
                        }

                        bufferedValues.offer(value)

                    } else {
                        emitter.onNext(value)
                    }
                }

                override fun onError(error: Throwable) {
                    if (done.value) {
                        handleReaktiveError(error)
                        return
                    }
                    done.value = true
                    compositeDisposable.dispose()
                    emitter.onError(error)
                }

                override fun onComplete() {
                    if (done.value) {
                        return
                    }

                    done.value = true
                    compositeDisposable.dispose()
                    emitter.onComplete()
                }
            }
        )
    }
