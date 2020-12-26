/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.pc.paging

import com.badoo.reaktive.observable.*
import dev.garage.rpm.base.lpc.contentIsEmpty

fun <T> Paging<T>.contentChanges(): Observable<List<T>> {
    return state
        .filter { it.content != null }
        .map { it.content!! }
        .distinctUntilChanged { l1, l2 -> l1 === l2 }
}

fun <T> Paging<T>.loadingChanges(): Observable<Boolean> {
    return state
        .map { it.loading }
        .distinctUntilChanged()
}

fun <T> Paging<T>.errorChanges(): Observable<Throwable> {
    return state
        .filter { it.error != null }
        .map { it.error!! }
        .distinctUntilChanged()
}

fun <T> Paging<T>.refreshErrorChanges(): Observable<Throwable> {
    return state
        .filter { it.error != null && it.content != null }
        .map { it.error!! }
        .distinctUntilChanged()
}

fun <T> Paging<T>.contentVisible(): Observable<Boolean> {
    return state.map {
        it.content != null && contentIsEmpty(it.content)
            .not()
    }.distinctUntilChanged()
}

fun <T> Paging<T>.emptyVisible(): Observable<Boolean> {
    return state.map {
        it.content != null && contentIsEmpty(it.content)
    }.distinctUntilChanged()
}

fun <T> Paging<T>.errorVisible(): Observable<Boolean> {
    return state.map {
        it.content == null && it.error != null
    }.distinctUntilChanged()
}

fun <T> Paging<T>.isLoading(): Observable<Boolean> {
    return state.map {
        it.content == null && it.loading
    }.distinctUntilChanged()
}

fun <T> Paging<T>.isRefreshing(): Observable<Boolean> {
    return state.map {
        it.content != null && it.loading
    }.distinctUntilChanged()
}

fun <T> Paging<T>.refreshEnabled(): Observable<Boolean> {
    return state.map {
        val isContentNull = it.content == null
        if (isContentNull) false else (it.loading && isContentNull).not()
    }.distinctUntilChanged()
}

fun <T> Paging<T>.pageLoadingVisible(): Observable<Boolean> {
    return state
        .map { it.pageLoading }
        .distinctUntilChanged()
}

fun <T> Paging<T>.pagingErrorChanges(): Observable<Throwable> {
    return state
        .filter { it.pagingError != null }
        .map { it.pagingError!! }
        .distinctUntilChanged()
}

fun <T> Paging<T>.pagingErrorVisible(): Observable<Boolean> {
    return state
        .map { it.pagingError != null }
        .distinctUntilChanged()
}

fun <T> Paging<T>.pageInAction(): Observable<Boolean> {
    return state
        .map { it.pageLoading || it.pagingError != null }
        .distinctUntilChanged()
}

fun <T> Paging<T>.isEndReached(): Observable<Boolean> {
    return state
        .map { it.isEndReached }
        .distinctUntilChanged()
}

fun <T> Paging<T>.scrollToTop(): Observable<Unit> {
    return contentChanges()
        .scan(emptyList<T>() to emptyList<T>()) { pair, list ->
            pair.second to list
        }
        .map { it.second.size <= it.first.size }
        .filter { it }
        .map { Unit }
}
