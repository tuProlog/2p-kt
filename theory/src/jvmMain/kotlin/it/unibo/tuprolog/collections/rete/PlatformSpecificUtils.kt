package it.unibo.tuprolog.collections.rete

internal actual fun <T> Sequence<T>.takeFirstAfterSkipping(n: Int): T = drop(n).first()
