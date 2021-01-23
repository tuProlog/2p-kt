package it.unibo.tuprolog.utils

interface Indexed<K, T> {
    val index: K

    val value: T

    operator fun component1(): K = index

    operator fun component2(): T = value

    fun <R> map(mapper: (T) -> R): Indexed<K, R>
}
