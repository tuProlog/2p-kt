package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Atomic : TypeTester<ExecutionContext>("atomic") {
    override fun testType(term: Term): Boolean = term is Constant
}