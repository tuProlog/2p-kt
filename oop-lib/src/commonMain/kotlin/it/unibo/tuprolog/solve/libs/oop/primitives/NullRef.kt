package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.NullRef as NullRefTerm

/** `null_ref(?Term)`: succeeds iff `Term` is [it.unibo.tuprolog.solve.libs.oop.ObjectRef.NULL]. */
object NullRef : TypeTester<ExecutionContext>("null_ref") {
    override val help: String =
        """
        `null_ref(@Term)`
        
        Succeeds if `Term` is the null reference, i.e. the term representing a JVM/Kotlin `null` value (as returned, for instance, by a method returning `null`). Fails otherwise, including when `Term` is unbound.

        **Examples**

        ```prolog
        ?- new_object('java.util.HashMap', [], M), invoke_method(M, get(k), V), null_ref(V).
        yes.

        ?- null_ref(foo).
        no.

        ?- null_ref(X).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is NullRefTerm
}
