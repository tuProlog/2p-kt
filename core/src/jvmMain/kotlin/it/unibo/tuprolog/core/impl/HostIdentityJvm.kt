package it.unibo.tuprolog.core.impl

import java.lang.ref.WeakReference

internal actual fun sameHost(
    a: Any,
    b: Any,
): Boolean = a === b

internal actual fun hostIdentityHashCode(value: Any): Int = System.identityHashCode(value)

internal actual fun compareHostIdentities(
    a: Any,
    b: Any,
): Int {
    if (a === b) return 0
    val byHash = System.identityHashCode(a).compareTo(System.identityHashCode(b))
    return if (byHash != 0) byHash else IdentityHashTieBreaker.rank(a).compareTo(IdentityHashTieBreaker.rank(b))
}

/**
 * Assigns stable ranks to distinct objects sharing the same identity hash code, which is rare. Objects are only held
 * weakly, so ranking them does not extend their lifetime.
 */
private object IdentityHashTieBreaker {
    private class Ranked(
        value: Any,
        val rank: Long,
    ) {
        val reference = WeakReference(value)
    }

    private val buckets = HashMap<Int, MutableList<Ranked>>()

    private var nextRank = 0L

    @Synchronized
    fun rank(value: Any): Long {
        val bucket = buckets.getOrPut(System.identityHashCode(value)) { mutableListOf() }
        bucket.removeAll { it.reference.get() == null }
        return bucket.firstOrNull { it.reference.get() === value }?.rank
            ?: Ranked(value, nextRank++).also { bucket.add(it) }.rank
    }
}
