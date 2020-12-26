/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.base.lpc

interface Emptyable {
    fun isEmpty(): Boolean
}

fun contentIsEmpty(content: Any?): Boolean {
    return when (content) {
        is Collection<*> -> content.isEmpty()
        is Array<*> -> content.isEmpty()
        is Emptyable -> content.isEmpty()
        else -> false
    }
}
