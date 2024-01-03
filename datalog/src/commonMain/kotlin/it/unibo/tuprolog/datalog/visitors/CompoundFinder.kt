package it.unibo.tuprolog.datalog.visitors

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

object CompoundFinder : AbstractClauseVisitor<Boolean>() {
    override fun reduce(results: Sequence<Boolean>): Boolean = results.any { it }

    override fun defaultValue(term: Term): Boolean = false

    override fun visitStruct(term: Struct): Boolean = term.arity > 0
}
