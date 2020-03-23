package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

object Atom : TypeTester<ExecutionContext>("atom") {
    override fun testType(term: Term): Boolean = term is it.unibo.tuprolog.core.Atom
}