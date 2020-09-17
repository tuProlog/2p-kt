package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Var : TypeTester<ExecutionContext>("var") {
    override fun testType(term: Term): Boolean = term is it.unibo.tuprolog.core.Var
}
