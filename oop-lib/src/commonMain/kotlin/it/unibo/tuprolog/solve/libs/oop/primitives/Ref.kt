package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.Ref as RefTerm

/** `ref(?Term)`: succeeds iff `Term` is an [it.unibo.tuprolog.solve.libs.oop.Ref] (an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] or a [it.unibo.tuprolog.solve.libs.oop.TypeRef]). */
object Ref : TypeTester<ExecutionContext>("ref") {
    override val help: String =
        """
        `ref(@Term)`
        
        Succeeds if `Term` is a reference of any kind: either an object reference (see `object_ref/1`, including the null reference) or a type reference (see `type_ref/1`). Fails otherwise, including when `Term` is unbound. References are opaque atomic terms that can be passed around, unified, and used as receivers of method calls.
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is RefTerm
}
