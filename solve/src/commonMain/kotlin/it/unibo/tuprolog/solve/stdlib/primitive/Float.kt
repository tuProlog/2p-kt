package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Float : TypeTester<ExecutionContext>("float") {
    override fun testType(term: Term): Boolean = term is Real
}
