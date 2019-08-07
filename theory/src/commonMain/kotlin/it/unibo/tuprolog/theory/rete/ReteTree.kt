package it.unibo.tuprolog.theory.rete

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.rete.clause.RootNode

/** A factory singleton for Rete Trees */
internal object ReteTree {

    /** Creates a ReteTree from give clauses */
    fun of(clauses: Iterable<Clause>): ReteNode<*, Clause> =
            RootNode().apply { clauses.forEach { put(it) } }

    /** Creates a ReteTree from give clauses */
    fun of(vararg clauses: Clause): ReteNode<*, Clause> = of(listOf(*clauses))
}
