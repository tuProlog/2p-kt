package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object NonVar : TypeTester<ExecutionContext>("nonvar") {
    override fun testType(term: Term): Boolean = term !is it.unibo.tuprolog.core.Var
}