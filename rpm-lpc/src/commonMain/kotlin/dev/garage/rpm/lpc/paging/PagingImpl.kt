/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.lpc.paging

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

class PagingImpl<T>(
    private val pageValue: Int,
    private val limit: Int,
    private val pageSource: ((page: Int, limit: Int, offset: Int, lastPage: Paging.Page<T>?) -> Single<Paging.Page<T>>)
) : Paging<T> {

    override val state: Observable<Paging.State<T>>
    override val actions: Consumer<Paging.Action> = object : Consumer<Paging.Action> {
        override fun onNext(value: Paging.Action) {
            actionSubject.onNext(value)
        }
    }

    private var page: Int = pageValue

    private val stateSubject: BehaviorSubject<Paging.State<T>> = BehaviorSubject(Paging.State())
    private val actionSubject: PublishSubject<Paging.Action> = PublishSubject()

    init {
        state = actionSubject.withLatestFrom(
            stateSubject,
            mapper = { action, state ->
                Pair(action, state)
            })
            .filter { (action, state) ->
                when (action) {
                    Paging.Action.LOAD_NEXT_PAGE,
                    Paging.Action.RETRY_LOAD_NEXT_PAGE -> {
                        state.loading.not() && state.pageLoading.not() && state.isEndReached.not()
                    }
                    Paging.Action.REFRESH -> state.loading.not()
                    Paging.Action.FORCE_REFRESH -> true
                }
            }
            .switchMap { (action, state) ->
                when (action) {
                    Paging.Action.REFRESH,
                    Paging.Action.FORCE_REFRESH -> {
                        page = pageValue
                        getPageSourceForRefreshAction(page, limit, action)
                    }
                    Paging.Action.LOAD_NEXT_PAGE -> {
                        page++
                        getPageSourceForPagingAction(
                            page,
                            limit,
                            state.content!!.size,
                            state.lastPage
                        )
                    }
                    Paging.Action.RETRY_LOAD_NEXT_PAGE -> {
                        page = (state.content!!.size / limit) + pageValue
                        getPageSourceForPagingAction(
                            page,
                            limit,
                            state.content.size,
                            state.lastPage
                        )
                    }
                }
            }
            .observeOn(mainScheduler)
            .scan(Paging.State<T>()) { state, action ->
                when (action) {
                    is InternalAction.RefreshStart -> {
                        if (action.force) {
                            state.copy(
                                content = null,
                                loading = true,
                                pageLoading = false,
                                pagingError = null,
                                error = null,
                                lastPage = null
                            )
                        } else {
                            state.copy(
                                loading = true,
                                pageLoading = false,
                                pagingError = null,
                                error = null
                            )
                        }
                    }
                    is InternalAction.RefreshFail -> {
                        state.copy(
                            loading = false,
                            error = action.error
                        )
                    }
                    is InternalAction.RefreshSuccess<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        val page = action.page as Paging.Page<T>

                        Paging.State(
                            content = page.items,
                            lastPage = page
                        )
                    }
                    InternalAction.PageLoadingStart -> {
                        state.copy(
                            pageLoading = true,
                            pagingError = null
                        )
                    }
                    is InternalAction.PageLoadingFail -> {
                        state.copy(
                            pageLoading = false,
                            pagingError = action.error
                        )
                    }
                    is InternalAction.PageLoadingSuccess<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        val page = action.page as Paging.Page<T>

                        state.copy(
                            pageLoading = false,
                            pagingError = null,
                            content = state.content?.plus(page.items),
                            lastPage = page
                        )
                    }
                }
            }
            .distinctUntilChanged { s1, s2 ->
                (s1.content === s2.content) &&
                        (s1.loading == s2.loading) &&
                        (s1.error === s2.error) &&
                        (s1.pageLoading == s2.pageLoading) &&
                        (s1.pagingError === s2.pagingError) &&
                        (s1.lastPage === s2.lastPage)
            }
            .doOnBeforeNext {
                stateSubject.onNext(it)
            }
            .replay(1)
            .refCount()
    }

    private fun getPageSourceForPagingAction(
        page: Int,
        limit: Int,
        offset: Int,
        lastPage: Paging.Page<T>?
    ): Observable<InternalAction> = pageSource(page, limit, offset, lastPage)
        .asObservable()
        .map { InternalAction.PageLoadingSuccess(it) }
        .startWithValue(InternalAction.PageLoadingStart)
        .onErrorReturn { InternalAction.PageLoadingFail(it) }

    private fun getPageSourceForRefreshAction(
        page: Int,
        limit: Int,
        action: Paging.Action
    ): Observable<InternalAction> = pageSource(page, limit, 0, null)
        .asObservable()
        .map { InternalAction.RefreshSuccess(it) }
        .startWithValue(InternalAction.RefreshStart(force = action == Paging.Action.FORCE_REFRESH))
        .onErrorReturn { InternalAction.RefreshFail(it) }

    private sealed class InternalAction {
        class RefreshStart(val force: Boolean) : InternalAction()
        class RefreshSuccess<T>(val page: Paging.Page<T>) : InternalAction()
        class RefreshFail(val error: Throwable) : InternalAction()
        object PageLoadingStart : InternalAction()
        class PageLoadingSuccess<T>(val page: Paging.Page<T>) : InternalAction()
        class PageLoadingFail(val error: Throwable) : InternalAction()
    }
}
