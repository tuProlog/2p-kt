package it.unibo.tuprolog.theory.rete

import it.unibo.tuprolog.core.Clause

/**
 * A factory object for Rete Trees
 */
internal object ReteTree {

    fun of(clauses: Iterable<Clause>): ReteNode<*, Clause> =
            RootNode().apply { clauses.forEach { put(it) } }

    fun of(vararg clauses: Clause): ReteNode<*, Clause> = of(listOf(*clauses))
}
