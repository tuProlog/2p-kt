package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object Atomic : TypeTester<ExecutionContext>("atomic") {
    override fun testType(term: Term): Boolean = term is Constant
}