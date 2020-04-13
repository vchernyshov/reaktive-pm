package dev.garage.rpm.util.queue

import dev.garage.rpm.util.atomic.*

internal class CopyOnWriteQueue<T>(initialList: List<T> = emptyList()) : Queue<T> {

    private val delegate = AtomicList(initialList)
    override val peek: T? get() = delegate.firstOrNull()
    override val size: Int get() = delegate.size
    override val isEmpty: Boolean get() = delegate.isEmpty

    override fun offer(item: T) {
        delegate += item
    }

    override fun poll(): T? {
        val item: T? = delegate.firstOrNull()
        delegate.value = delegate.value.drop(1)

        return item
    }

    override fun clear() {
        delegate.clear()
    }

    override fun iterator(): Iterator<T> = delegate.iterator()
}
