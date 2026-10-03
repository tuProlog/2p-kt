package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Number : TypeTester<ExecutionContext>("number") {
    override val help: String =
        """
        `number(@Term)`
        
        Succeeds if `Term` is numeric, either an integer or a real value; fails otherwise.

        **Examples**

        ```prolog
        ?- number(3.14).
        yes.

        ?- number(42).
        yes.

        ?- number('42').
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is Numeric
}
