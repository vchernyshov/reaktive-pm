package dev.garage.rpm.util

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.utils.handleReaktiveError

internal fun <T> Observable<T>.bufferSingleValueWhileIdle(isIdleObservable: Observable<Boolean>): Observable<T> =
    observable { emitter ->

        val compositeDisposable = CompositeDisposable()
        emitter.setDisposable(compositeDisposable)

        var done = false

        var isIdle = false
        var bufferedValue: T? = null

        subscribe(
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable += disposable
                    compositeDisposable += isIdleObservable
                        .subscribe {
                            if (it) {
                                isIdle = true
                            } else {
                                isIdle = false
                                bufferedValue?.let { value ->
                                    onNext(value)
                                }
                                bufferedValue = null
                            }
                        }
                }

                override fun onNext(value: T) {
                    if (done) {
                        return
                    }

                    if (isIdle) {
                        bufferedValue = value
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