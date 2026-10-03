package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Atomic : TypeTester<ExecutionContext>("atomic") {
    override val help: String =
        """
        `atomic(@Term)`
        
        Succeeds if `Term` is an atomic constant, such as an atom or number; fails otherwise.

        **Examples**

        ```prolog
        ?- atomic(foo).
        yes.

        ?- atomic(42).
        yes.

        ?- atomic(f(x)).
        no.

        ?- atomic(X).
        no.
        ```
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is Constant
}
