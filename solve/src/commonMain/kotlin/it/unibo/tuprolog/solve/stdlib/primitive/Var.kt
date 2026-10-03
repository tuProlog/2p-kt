package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Var : TypeTester<ExecutionContext>("var") {
    override val help: String =
        """
        `var(@Term)`
        
        Succeeds if `Term` is an unbound variable; fails otherwise.

        **Examples**

        ```prolog
        ?- var(X).
        yes.

        ?- X = a, var(X).
        no.

        ?- var(f(X)).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is it.unibo.tuprolog.core.Var
}
