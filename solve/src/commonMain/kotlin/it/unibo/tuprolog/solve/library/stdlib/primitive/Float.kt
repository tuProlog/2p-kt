package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object Float : TypeTester<ExecutionContext>("float") {
    override fun testType(term: Term): Boolean = term is Real
}