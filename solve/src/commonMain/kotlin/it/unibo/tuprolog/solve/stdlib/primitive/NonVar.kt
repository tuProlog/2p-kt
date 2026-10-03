package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object NonVar : TypeTester<ExecutionContext>("nonvar") {
    override val help: String =
        """
        `nonvar(@Term)`
        
        Succeeds if `Term` is not a variable; fails otherwise.

        **Examples**

        ```prolog
        ?- nonvar(f(X)).
        yes.

        ?- nonvar(X).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term !is it.unibo.tuprolog.core.Var
}
