package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.ObjectRef as ObjectRefTerm

object ObjectRef : TypeTester<ExecutionContext>("object_ref") {
    override fun testType(term: Term): Boolean = term is ObjectRefTerm
}
