/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.lc.loading

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.observable.Observable

interface Loading<T> {

    enum class Action { REFRESH, FORCE_REFRESH }

    val state: Observable<State<T>>

    val actions: Consumer<Action>

    data class State<T>(
        val content: T? = null,
        val loading: Boolean = false,
        val error: Throwable? = null
    )
}
