package it.unibo.tuprolog.collections.rete

internal actual fun <T> Sequence<T>.takeFirstAfterSkipping(n: Int): T {
    val iter = this.iterator()
    for (i in 0 until n) iter.next()
    return iter.next()
}
