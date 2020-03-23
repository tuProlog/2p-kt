package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object Ground : TypeTester<ExecutionContext>("ground") {
    override fun testType(term: Term): Boolean = term.isGround
}