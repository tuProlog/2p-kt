package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Compound : TypeTester<ExecutionContext>("compound") {
    override val help: String =
        """
        `compound(@Term)`
        
        Succeeds if `Term` is a compound structure with one or more arguments; fails otherwise.

        **Examples**

        ```prolog
        ?- compound(f(a)).
        yes.

        ?- compound([a]).
        yes.

        ?- compound(foo).
        no.

        ?- compound(X).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is Struct && term.arity > 0
}
