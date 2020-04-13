package dev.garage.rpm.util.atomic

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update

internal typealias AtomicList<T> = AtomicReference<List<T>>

internal fun <T> atomicList(initialList: List<T> = emptyList()): AtomicList<T> = AtomicList(initialList)

internal fun <T> AtomicList<T>.add(element: T) {
    update { it + element }
}

internal operator fun <T> AtomicList<T>.plusAssign(element: T) {
    add(element)
}

internal fun <T> AtomicList<T>.add(index: Int, element: T) {
    update { it.insert(index, element) }
}

internal fun <T> AtomicList<T>.removeAt(index: Int): T =
    getAndUpdate {
        it.filterIndexed { i, _ -> i != index }
    }[index]

internal fun <T> AtomicList<T>.remove(element: T): Boolean {
    var removed = false

    update {
        val newList = it - element
        removed = newList.size < it.size
        newList
    }

    return removed
}

internal operator fun <T> AtomicList<T>.minusAssign(element: T) {
    remove(element)
}

internal fun <T> AtomicList<T>.clear() {
    update { emptyList() }
}

internal operator fun <T> AtomicList<T>.get(index: Int): T = value[index]

internal operator fun <T> AtomicList<T>.set(index: Int, element: T): T =
    getAndUpdate {
        it.replace(index, element)
    }[index]

internal fun <T> AtomicList<T>.firstOrNull(): T? = value.firstOrNull()

internal val AtomicReference<out Collection<*>>.size: Int get() = value.size

internal val AtomicReference<out Collection<*>>.isEmpty: Boolean get() = value.isEmpty()

internal val AtomicReference<out Collection<*>>.isNotEmpty: Boolean get() = value.isNotEmpty()

internal operator fun <T> AtomicReference<out Iterable<T>>.iterator(): Iterator<T> = value.iterator()

internal fun <T> List<T>.replace(index: Int, element: T): List<T> =
    ArrayList(this)
        .apply { set(index, element) }

internal fun <T> List<T>.insert(index: Int, element: T): List<T> =
    when {
        (index < 0) || (index > size) -> throw IndexOutOfBoundsException("Index: $index, size: $size")
        index == size -> plus(element)

        else ->
            ArrayList<T>(size + 1)
                .also { list ->
                    forEachIndexed { i, item ->
                        if (i == index) {
                            list.add(element)
                        }
                        list.add(item)
                    }
                }
    }