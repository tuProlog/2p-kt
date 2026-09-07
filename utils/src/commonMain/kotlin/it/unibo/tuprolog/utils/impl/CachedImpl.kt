package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.Optional

/**
 * Default [Cached] implementation returned by [Cached.of], memoizing the result of [generator] in an
 * [Optional] until [invalidate]d. Not meant to be instantiated directly; use [Cached.of] instead.
 */
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
