/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.lpc.loading

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.distinctUntilChanged
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.onErrorReturn
import com.badoo.reaktive.observable.refCount
import com.badoo.reaktive.observable.replay
import com.badoo.reaktive.observable.scan
import com.badoo.reaktive.observable.startWithValue
import com.badoo.reaktive.observable.switchMap
import com.badoo.reaktive.observable.withLatestFrom
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.publish.PublishSubject

class LoadingImpl<T>(
    source: Single<T>
) : Loading<T> {

    override val state: Observable<Loading.State<T>>
    override val actions: Consumer<Loading.Action> = object : Consumer<Loading.Action> {
        override fun onNext(value: Loading.Action) {
            actionSubject.onNext(value)
        }
    }

    private val stateSubject: BehaviorSubject<Loading.State<T>> =
        BehaviorSubject(Loading.State())
    private val actionSubject: PublishSubject<Loading.Action> = PublishSubject()

    init {
        state = actionSubject.withLatestFrom(
            stateSubject,
            mapper = { action, state -> Pair(action, state) })
            .filter { (action, state) ->
                when (action) {
                    Loading.Action.REFRESH -> state.loading.not()
                    Loading.Action.FORCE_REFRESH -> true
                }
            }
            .switchMap { (action, _) ->
                source
                    .asObservable()
                    .map { InternalAction.LoadSuccess(it) }
                    .startWithValue(InternalAction.LoadStart(force = action == Loading.Action.FORCE_REFRESH))
                    .onErrorReturn { InternalAction.LoadFail(it) }
            }
            .observeOn(mainScheduler)
            .scan(Loading.State<T>()) { state, action ->
                when (action) {
                    is InternalAction.LoadStart -> {
                        state.copy(
                            content = if (action.force) null else state.content,
                            loading = true,
                            error = null
                        )
                    }
                    is InternalAction.LoadSuccess<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        state.copy(
                            loading = false,
                            content = action.data as T
                        )
                    }
                    is InternalAction.LoadFail -> {
                        state.copy(
                            loading = false,
                            error = action.error
                        )
                    }
                }
            }
            .distinctUntilChanged()
            .doOnBeforeNext { stateSubject.onNext(it) }
            .replay(1)
            .refCount()
    }

    private sealed class InternalAction {
        class LoadStart(val force: Boolean) : InternalAction()
        class LoadSuccess<T>(val data: T) : InternalAction()
        class LoadFail(val error: Throwable) : InternalAction()
    }
}
