package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Compound : TypeTester<ExecutionContext>("compound") {
    override fun testType(term: Term): Boolean =
        term is Struct && term.arity > 0
}