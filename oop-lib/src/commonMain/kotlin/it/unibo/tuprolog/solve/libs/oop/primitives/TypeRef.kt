package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.TypeRef as TypeRefTerm

/** `type_ref(?Term)`: succeeds iff `Term` is a [it.unibo.tuprolog.solve.libs.oop.TypeRef]. */
object TypeRef : TypeTester<ExecutionContext>("type_ref") {
    override val help: String =
        """
        `type_ref(@Term)`
        
        Succeeds if `Term` is a type reference, i.e. a term wrapping a JVM/Kotlin class (rendered like `<type:java.lang.String>`), as produced by `type/2` or by `${'$'}Alias` references to types such as `${'$'}string`. Type references can be used to instantiate a class (`new_object/3`) or to invoke its static members. Fails otherwise, including when `Term` is unbound.
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is TypeRefTerm
}
