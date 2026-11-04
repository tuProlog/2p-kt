package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Term

/** Two term instances already compared during a unification run: equal to another pair of the very same instances. */
internal class ComparedPair(
    private val lhs: Term,
    private val rhs: Term,
) {
    override fun equals(other: Any?): Boolean = other is ComparedPair && lhs === other.lhs && rhs === other.rhs

    override fun hashCode(): Int = 31 * lhs.hashCode() + rhs.hashCode()
}
