package it.unibo.tuprolog.theory.rete.clause

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.rete.ReteNode
import kotlin.jvm.JvmStatic

/** A factory singleton for Rete Trees */
internal object ReteTree {

    /** Creates a ReteTree from give clauses */
    @JvmStatic
    fun of(clauses: Iterable<Clause>): ReteNode<*, Clause> =
        RootNode().apply { clauses.forEach { put(it) } }

    /** Creates a ReteTree from give clauses */
    @JvmStatic
    fun of(vararg clauses: Clause): ReteNode<*, Clause> = of(listOf(*clauses))
}
