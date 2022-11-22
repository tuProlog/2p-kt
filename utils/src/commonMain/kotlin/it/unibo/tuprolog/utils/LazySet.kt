package it.unibo.tuprolog.utils

class LazySet<T>(lazyItems: Sequence<T>) : Set<T> {
    private val items: MutableSet<T> = mutableSetOf()

    private val lazyItemsIterator: Iterator<T> = lazyItems.iterator()

    private inline fun forEachRemaining(action: (T) ->  Unit = {}) {
        while (lazyItemsIterator.hasNext()) {
            action(lazyItemsIterator.next())
        }
    }

    private fun addAllRemaining() {
        forEachRemaining {
            items.add(it)
        }
    }

    override val size: Int by lazy {
        addAllRemaining()
        items.size
    }

    override fun contains(element: T): Boolean {
        if (element in items) {
            return true
        }
        forEachRemaining {
            items.add(it)
            if (it == element) {
                return@contains true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.any { contains(it) }
    }

    override fun isEmpty(): Boolean {
        if (items.isNotEmpty()) {
            return false
        }
        forEachRemaining {
            if (items.add(it)) {
                return false
            }
        }
        return true
    }

    private fun findNext(): Optional<out T> {
        forEachRemaining {
            if (items.add(it)) {
                return@findNext Optional.some(it)
            }
        }
        return Optional.none()
    }

    override fun iterator(): Iterator<T> =
        object : Iterator<T> {
            private var internalIterator: Iterator<T>? = items.iterator()
            private lateinit var next: Optional<out T>

            override fun hasNext(): Boolean {
                if (internalIterator?.hasNext() == true) {
                    return true
                }
                internalIterator = null
                next = findNext()
                return next.isPresent
            }

            override fun next(): T {
                return internalIterator?.next()
                    ?: next.takeIf { it.isPresent }?.valueOrError
                    ?: throw NoSuchElementException("Iterator is over")
            }
        }

    override fun hashCode(): Int {
        addAllRemaining()
        return items.hashCode()
    }

    override fun toString(): String {
        if (items.isEmpty()) {
            return if (lazyItemsIterator.hasNext()) "{ ... }" else "{ }"
        }
        return items.joinToString(
            prefix = "{ ",
            separator = ", ",
            postfix = if (lazyItemsIterator.hasNext()) ", ... }" else " }"
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other === this) return true
        if (other !is Set<*>) return false
        addAllRemaining()
        return items == other
    }
}
