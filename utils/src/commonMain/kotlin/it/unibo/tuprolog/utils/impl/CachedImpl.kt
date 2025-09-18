package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.Optional

data class CachedImpl<T>(
    private val generator: () -> T,
) : Cached<T> {
    private var cached: Optional<out T> = Optional.none()

    override val isValid: Boolean
        get() = cached.isPresent

    override val isInvalid: Boolean
        get() = cached.isAbsent

    override val value: T
        get() {
            regenerate()
            return cached.value!!
        }

    override fun regenerate() {
        if (isInvalid) {
            cached = Optional.some(generator())
        }
    }

    override fun invalidate() {
        cached = Optional.none()
    }
}
