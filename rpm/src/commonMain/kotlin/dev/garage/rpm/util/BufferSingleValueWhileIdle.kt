package dev.garage.rpm.util

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.handleReaktiveError

internal fun <T> Observable<T>.bufferSingleValueWhileIdle(isIdleObservable: Observable<Boolean>): Observable<T> =
    observable { emitter ->

        val compositeDisposable = CompositeDisposable()
        emitter.setDisposable(compositeDisposable)

        val done = AtomicBoolean(false)

        val isIdle = AtomicBoolean(false)
        val bufferedValue = AtomicReference<T?>(null)

        subscribe(
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable += disposable
                    compositeDisposable += isIdleObservable
                        .subscribe {
                            if (it) {
                                isIdle.value = true
                            } else {
                                isIdle.value = false
                                bufferedValue.value?.let { value ->
                                    onNext(value)
                                }
                                bufferedValue.getAndSet(null)
                            }
                        }
                }

                override fun onNext(value: T) {
                    if (done.value) {
                        return
                    }

                    if (isIdle.value) {
                        bufferedValue.getAndSet(value)
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