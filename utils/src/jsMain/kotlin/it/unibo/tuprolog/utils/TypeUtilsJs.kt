package it.unibo.tuprolog.utils

@Suppress("UnsafeCastFromDynamic")
actual fun <T> Any.forceCast(): T {
    @Suppress("UnnecessaryVariable")
    val it: dynamic = this
    return it
}
