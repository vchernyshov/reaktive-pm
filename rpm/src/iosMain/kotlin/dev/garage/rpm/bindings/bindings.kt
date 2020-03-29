package dev.garage.rpm.bindings

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.cstr
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*
import platform.darwin.NSObject
import platform.objc.OBJC_ASSOCIATION_RETAIN
import platform.objc.objc_removeAssociatedObjects
import platform.objc.objc_setAssociatedObject
import kotlin.native.ref.WeakReference

fun UIButton.clicks(): Observable<Unit> =
    observable { emitter ->
        val handler: (UIButton) -> Unit = {
            if (!emitter.isDisposed) {
                emitter.onNext(Unit)
            }
        }
        setEventHandler(UIControlEventTouchUpInside, handler)
        emitter.setDisposable(Disposable {
            removeEventHandler(UIControlEventTouchUpInside)
        })
    }

fun UITextField.focus(): ((Boolean) -> Unit) = {
    if (it) {
        becomeFirstResponder()
    } else {
        if (nextResponder?.canBecomeFirstResponder == true) {
            nextResponder?.becomeFirstResponder()
        } else {
            resignFirstResponder()
        }
    }
}

fun UITextField.focusChanges(): Observable<Boolean> =
    observable { emitter ->
        val handler: UITextField.() -> Unit = {
            if (!emitter.isDisposed) {
                emitter.onNext(isFocused())
            }
        }

        setEventHandler(UIControlEventEditingDidBegin, handler)
        setEventHandler(UIControlEventEditingDidEnd, handler)
        setEventHandler(UIControlEventEditingDidEndOnExit, handler)

        emitter.onNext(isFocused())
        emitter.setDisposable(Disposable {
            removeEventHandler(UIControlEventEditingDidBegin)
            removeEventHandler(UIControlEventEditingDidEnd)
            removeEventHandler(UIControlEventEditingDidEndOnExit)
        })
    }

fun UITextField.textChanges(): Observable<String> =
    observable { emitter ->
        val handler: (UITextField) -> Unit = {
            if (!emitter.isDisposed) {
                emitter.onNext(text().orEmpty())
            }
        }
        setEventHandler(UIControlEventEditingChanged, handler)
        emitter.onNext(text().orEmpty())
        emitter.setDisposable(Disposable {
            removeEventHandler(UIControlEventEditingChanged)
        })
    }

fun <T : UIControl> T.removeEventHandler(event: UIControlEvents) {
    removeTarget(
        target = this,
        action = NSSelectorFromString("action"),
        forControlEvents = event
    )
    objc_removeAssociatedObjects(
        `object` = this
    )
}

fun <T : UIControl> T.setEventHandler(event: UIControlEvents, lambda: T.() -> Unit) {
    val lambdaTarget = ControlLambdaTarget(this, lambda)

    addTarget(
        target = lambdaTarget,
        action = NSSelectorFromString("action"),
        forControlEvents = event
    )

    objc_setAssociatedObject(
        `object` = this,
        key = "event$event".cstr,
        value = lambdaTarget,
        policy = OBJC_ASSOCIATION_RETAIN
    )
}

@ExportObjCClass
private class ControlLambdaTarget<T : Any>(
    ref: T,
    val lambda: T.() -> Unit
) : NSObject() {
    private val weakRef = WeakReference(ref)

    @ObjCAction
    fun action() {
        val ref = weakRef.get() ?: return
        lambda.invoke(ref)
    }
}