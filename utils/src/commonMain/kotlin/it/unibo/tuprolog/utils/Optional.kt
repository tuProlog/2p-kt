package it.unibo.tuprolog.utils

sealed class Optional<T> {
    companion object {
        fun <T> some(value: T): Some<out T> = Some(value)

        fun <T> of(value: T?): Optional<out T> =
            if (value == null) {
                none()
            } else {
                some(value)
            }

        fun <T> none(): Optional<out T> = None
    }

    data class Some<T>(
        override val value: T,
    ) : Optional<T>() {
        override val isPresent: Boolean
            get() = true

        override fun <R> map(function: (T) -> R): Some<R> = Some(function(value))

        override fun filter(predicate: (T) -> Boolean): Optional<out T> =
            when {
                predicate(value) -> this
                else -> None
            }

        override fun toSequence(): Sequence<T> = sequenceOf(value)

        override fun toString(): String = "Some($value)"
    }

    object None : Optional<Nothing>() {
        override val value: Nothing?
            get() = null

        override val isPresent: Boolean
            get() = false

        override fun <R> map(function: (Nothing) -> R): None = None

        override fun filter(predicate: (Nothing) -> Boolean): None = None

        override fun toSequence(): Sequence<Nothing> = emptySequence()

        override fun toString(): String = "None"
    }

    abstract val value: T?

    abstract val isPresent: Boolean

    val isAbsent: Boolean
        get() = !isPresent

    abstract fun <R> map(function: (T) -> R): Optional<out R>

    abstract fun filter(predicate: (T) -> Boolean): Optional<out T>

    abstract fun toSequence(): Sequence<T>

    abstract override fun toString(): String
}
