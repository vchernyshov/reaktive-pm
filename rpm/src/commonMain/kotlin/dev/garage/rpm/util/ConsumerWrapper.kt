package dev.garage.rpm.util

import com.badoo.reaktive.base.Consumer

/**
 * Wrappers are normally exposed to Swift.
 * You might want to enable Objective-C generics,
 * please refer to the [documentation][https://kotlinlang.org/docs/reference/native/objc_interop.html#to-use]
 * for more information.
 * You can also extend the wrapper class if you need to expose any additional operators.
 */
// TODO: split for to classes: ConsumerWrapper and LambdaWrapper or create factory methods
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