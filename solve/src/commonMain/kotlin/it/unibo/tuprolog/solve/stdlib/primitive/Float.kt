package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Float : TypeTester<ExecutionContext>("float") {
    override val help: String =
        """
        `float(@Term)`
        
        Succeeds if `Term` is a floating-point number; fails otherwise.

        **Examples**

        ```prolog
        ?- float(1.5).
        yes.

        ?- float(1).
        no.

        ?- float(X).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is Real
}
