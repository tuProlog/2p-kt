package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.TypeRef as TypeRefTerm

/** `type_ref(?Term)`: succeeds iff `Term` is a [it.unibo.tuprolog.solve.libs.oop.TypeRef]. */
object TypeRef : TypeTester<ExecutionContext>("type_ref") {
    override fun testType(term: Term): Boolean = term is TypeRefTerm
}
