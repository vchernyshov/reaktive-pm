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
import platform.objc.objc_setAssociatedObject
import kotlin.native.ref.WeakReference

fun UIButton.clicks(): Observable<Unit> =
    observable { emitter ->
        var handler: ((UIButton) -> Unit)? = {
            if (!emitter.isDisposed) {
                emitter.onNext(Unit)
            }
        }
        setEventHandler(UIControlEventTouchUpInside, handler)
        emitter.setDisposable(Disposable {
            setEventHandler(UIControlEventEditingChanged, null)
            handler = null
        })
    }

fun UITextField.focusChanges(): Observable<Boolean> =
    observable {

    }

fun UITextField.textChanges(): Observable<String> =
    observable { emitter ->
        var handler: ((UITextField) -> Unit)? = {
            if (!emitter.isDisposed) {
                emitter.onNext(it.text ?: "")
            }
        }
        setEventHandler(UIControlEventEditingChanged, handler)
        emitter.setDisposable(Disposable {
            setEventHandler(UIControlEventEditingChanged, null)
            handler = null
        })
    }

fun <T : UIControl> T.setEventHandler(event: UIControlEvents, lambda: (T.() -> Unit)?) {
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
    val lambda: (T.() -> Unit)?
) : NSObject() {
    private val weakRef = WeakReference(ref)

    @ObjCAction
    fun action() {
        val ref = weakRef.get() ?: return
        lambda?.invoke(ref)
    }
}