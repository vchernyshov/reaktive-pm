package dev.garage.rpm.app.utils

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun View.visibilityView(visibilityWhenFalse: Int = View.GONE): Consumer<in Boolean> {
    kotlin.require(visibilityWhenFalse != View.VISIBLE) {
        "Setting visibility to VISIBLE when false would have no effect."
    }
    kotlin.require(visibilityWhenFalse == View.INVISIBLE || visibilityWhenFalse == View.GONE) {
        "Must set visibility to INVISIBLE or GONE when false."
    }
    return object : Consumer<Boolean> {
        override fun onNext(value: Boolean) {
            visibility = if (value) View.VISIBLE else visibilityWhenFalse
        }
    }
}

fun SwipeRefreshLayout.refreshes(): Observable<Unit> = observable { emitter ->
    setOnRefreshListener {
        if (!emitter.isDisposed) {
            emitter.onNext(Unit)
        }
    }

    emitter.setDisposable(Disposable {
        setOnRefreshListener(null)
    })
}
