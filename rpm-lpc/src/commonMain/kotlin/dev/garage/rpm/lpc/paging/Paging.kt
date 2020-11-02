/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.lpc.paging

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.observable.Observable

interface Paging<T> {

    enum class Action { REFRESH, FORCE_REFRESH, LOAD_NEXT_PAGE, RETRY_LOAD_NEXT_PAGE }

    val state: Observable<State<T>>
    val actions: Consumer<Action>

    data class State<T>(
        val content: List<T>? = null,
        val loading: Boolean = false,
        val error: Throwable? = null,
        val pageLoading: Boolean = false,
        val pagingError: Throwable? = null,
        val lastPage: Page<T>? = null
    ) {

        val isEndReached: Boolean get() = lastPage?.isEndReached ?: false
    }

    interface Page<T> {
        val items: List<T>
        val lastItem: T? get() = items.lastOrNull()
        val isEndReached: Boolean
    }
}
