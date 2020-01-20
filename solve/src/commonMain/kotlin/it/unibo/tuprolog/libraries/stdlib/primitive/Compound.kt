package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object Compound : TypeTester<ExecutionContext>("compound") {
    override fun testType(term: Term): Boolean = term is Struct
}