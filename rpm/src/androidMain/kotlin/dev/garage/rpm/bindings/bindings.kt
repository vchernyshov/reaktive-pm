package dev.garage.rpm.bindings

import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

object AlwaysTrue : () -> Boolean, (Any) -> Boolean {
    override fun invoke() = true
    override fun invoke(ignored: Any) = true
}

// TODO: add main thread check

fun View.clicks(): Observable<Unit> = observable { emitter ->
    setOnClickListener {
        if (!emitter.isDisposed) {
            emitter.onNext(Unit)
        }
    }
    emitter.setDisposable(Disposable {
        setOnClickListener(null)
    })
}

fun View.focusChanges(): Observable<Boolean> = observable { emitter ->
    setOnFocusChangeListener { _, hasFocus ->
        if (!emitter.isDisposed) {
            emitter.onNext(hasFocus)
        }
    }
    emitter.onNext(hasFocus())
    emitter.setDisposable(Disposable {
        onFocusChangeListener = null
    })
}

fun CompoundButton.checkedChanges(): Observable<Boolean> = observable { emitter ->
    setOnCheckedChangeListener { _, isChecked ->
        if (!emitter.isDisposed) {
            emitter.onNext(isChecked)
        }
    }
    emitter.onNext(isChecked)
    emitter.setDisposable(Disposable {
        setOnCheckedChangeListener(null)
    })
}

fun EditText.textChanges(): Observable<CharSequence> = observable { emitter ->
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!emitter.isDisposed) {
                emitter.onNext(s)
            }
        }
    }
    addTextChangedListener(watcher)
    emitter.onNext(text)
    emitter.setDisposable(Disposable {
        removeTextChangedListener(watcher)
    })
}

fun TextView.editorActions(handled: (Int) -> Boolean = AlwaysTrue): Observable<Int> =
    observable { emitter ->
        setOnEditorActionListener { _, actionId, _ ->
            try {
                if (!emitter.isDisposed && handled(actionId)) {
                    emitter.onNext(actionId)
                    return@setOnEditorActionListener true
                }
            } catch (e: Exception) {
                emitter.onError(e)
                emitter.onComplete()
            }
            return@setOnEditorActionListener false
        }
        emitter.setDisposable(Disposable {
            setOnEditorActionListener(null)
        })
    }

fun Toolbar.itemClicks(): Observable<MenuItem> = observable { emitter ->
    setOnMenuItemClickListener { item ->
        if (!emitter.isDisposed) {
            emitter.onNext(item)
        }
        true
    }
    emitter.setDisposable(Disposable {
        setOnMenuItemClickListener(null)
    })
}

fun Toolbar.navigationClicks(): Observable<Unit> = observable { emitter ->
    setNavigationOnClickListener {
        if (!emitter.isDisposed) {
            emitter.onNext(Unit)
        }
    }
    emitter.setDisposable(Disposable {
        setNavigationOnClickListener(null)
    })
}