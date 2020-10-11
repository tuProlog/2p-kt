package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.Ref as RefTerm

object Ref : TypeTester<ExecutionContext>("ref") {
    override fun testType(term: Term): Boolean = term is RefTerm
}
