package it.unibo.tuprolog.collections.rete

internal actual fun <T> Sequence<T>.takeFirstAfterSkipping(n: Int): T {
    val iter = this.iterator()
    repeat(n) { iter.next() }
    return iter.next()
}
