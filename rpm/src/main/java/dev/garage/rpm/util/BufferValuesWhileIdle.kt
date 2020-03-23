package dev.garage.rpm.util

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.utils.handleReaktiveError
import java.util.*

internal fun <T> Observable<T>.bufferValuesWhileIdle(
    isIdleObservable: Observable<Boolean>,
    bufferSize: Int? = null
): Observable<T> =
    observable { emitter->

        val compositeDisposable = CompositeDisposable()
        emitter.setDisposable(compositeDisposable)

        var done = false

        var isIdle = false
        val bufferedValues: Queue<T> = LinkedList()

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable += disposable
                    compositeDisposable += isIdleObservable.subscribe {
                        if (it) {
                            isIdle = true
                        } else {
                            isIdle = false
                            bufferedValues.forEach { v ->
                                onNext(v)
                            }
                            bufferedValues.clear()
                        }
                    }
                }

                override fun onNext(value: T) {
                    if (done) {
                        return
                    }

                    if (isIdle) {

                        if (bufferedValues.size == bufferSize) {
                            bufferedValues.poll()
                        }

                        bufferedValues.offer(value)

                    } else {
                        emitter.onNext(value)
                    }
                }

                override fun onError(error: Throwable) {
                    if (done) {
                        handleReaktiveError(error)
                        return
                    }
                    done = true
                    compositeDisposable.dispose()
                    emitter.onError(error)
                }

                override fun onComplete() {
                    if (done) {
                        return
                    }

                    done = true
                    compositeDisposable.dispose()
                    emitter.onComplete()
                }
            }
        )
    }
