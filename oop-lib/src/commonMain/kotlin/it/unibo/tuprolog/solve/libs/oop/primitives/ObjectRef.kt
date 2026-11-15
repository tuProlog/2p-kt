package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.core.ObjectRef as ObjectRefTerm
import it.unibo.tuprolog.solve.libs.oop.TypeRef as TypeRefTerm

/** `object_ref(?Term)`: succeeds iff `Term` is an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]. */
object ObjectRef : TypeTester<ExecutionContext>("object_ref") {
    override val help: String =
        """
        `object_ref(@Term)`
        
        Succeeds if `Term` is an object reference, i.e. a term wrapping a non-null JVM/Kotlin object other than a type, as produced by `new_object/3`, `cast/3`, `invoke_strict/3` and friends. Neither the null reference (see `null_ref/1`) nor type references (see `type_ref/1`) are object references. Fails otherwise, including when `Term` is unbound.

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

    override fun testType(term: Term): Boolean = term is ObjectRefTerm && term !is TypeRefTerm
}
