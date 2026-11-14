package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import it.unibo.tuprolog.core.Ref as RefTerm

/** `ref(?Term)`: succeeds iff `Term` is an [it.unibo.tuprolog.solve.libs.oop.Ref] (an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] or a [it.unibo.tuprolog.solve.libs.oop.TypeRef]). */
object Ref : TypeTester<ExecutionContext>("ref") {
    override val help: String =
        """
        `ref(@Term)`
        
        Succeeds if `Term` is a reference of any kind: an object reference (see `object_ref/1`), the null reference (see `null_ref/1`) or a type reference (see `type_ref/1`). Fails otherwise, including when `Term` is unbound. References are opaque constants that can be passed around, unified (they unify when they reference the same object), and used as receivers of method calls.

        **Examples**

        ```prolog
        ?- type('java.lang.String', T), ref(T).
        yes.

        ?- new_object('java.util.ArrayList', [], L), ref(L).
        yes.

        ?- ref(hello).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is RefTerm
}
