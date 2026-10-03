package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.solve.libs.oop.ObjectRef as ObjectRefTerm

/** `object_ref(?Term)`: succeeds iff `Term` is an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]. */
object ObjectRef : TypeTester<ExecutionContext>("object_ref") {
    override val help: String =
        """
        `object_ref(@Term)`
        
        Succeeds if `Term` is an object reference, i.e. a term wrapping a JVM/Kotlin object (rendered like `<object:java.util.ArrayList#123>`), as produced by `new_object/3`, `cast/3`, `invoke_strict/3` and friends. The null reference counts as an object reference. Fails otherwise, including when `Term` is unbound.

        **Examples**

        ```prolog
        ?- new_object('java.util.ArrayList', [], L), object_ref(L).
        yes.

        ?- type('java.lang.String', T), object_ref(T).
        no.

        ?- object_ref(X).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is ObjectRefTerm
}
