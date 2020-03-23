package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object Callable : TypeTester<ExecutionContext>("callable") {
    override fun testType(term: Term): Boolean = term is Struct
}