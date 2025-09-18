package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.CachedImpl

interface Cached<T> {
    companion object {
        fun <T> of(generator: () -> T): Cached<T> = CachedImpl(generator)
    }

    val isValid: Boolean

    val isInvalid: Boolean

    val value: T

    fun regenerate()

    fun <R> regenerating(consumer: (T) -> R): R {
        regenerate()
        return value.let(consumer)
    }

    fun invalidate()

    fun <R> ifValid(consumer: (T) -> R): Optional<out R> =
        if (isValid) {
            Optional.of(value.let(consumer))
        } else {
            Optional.none()
        }
}
