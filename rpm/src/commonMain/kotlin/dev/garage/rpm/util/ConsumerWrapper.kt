package dev.garage.rpm.util

import com.badoo.reaktive.base.Consumer

// TODO: split for to classes: ConsumerWrapper and LambdaWrapper
class ConsumerWrapper<T> : Consumer<T> {

    private var innerConsumer: Consumer<T>? = null
    private var innerLambda: ((T) -> Unit)? = null

    constructor(inner: Consumer<T>) {
        innerConsumer = inner
    }

    constructor(inner: (T) -> Unit) {
        innerLambda = inner
    }

    override fun onNext(value: T) {
        innerConsumer?.onNext(value)
        innerLambda?.invoke(value)
    }
}