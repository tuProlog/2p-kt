package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Ground : TypeTester<ExecutionContext>("ground") {
    override val help: String =
        """
        `ground(@Term)`
        
        Succeeds if `Term` contains no variables; fails otherwise.

        **Examples**

        ```prolog
        ?- ground(f(a, b)).
        yes.

        ?- ground(f(a, X)).
        no.

        ?- ground([]).
        yes.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term.isGround
}
