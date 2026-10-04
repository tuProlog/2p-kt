package it.unibo.tuprolog.solve.stdlib.primitive

object Assert : AbstractAssert("", false) {
    override val help: String =
        """
        `assert(+Clause)`
        
        Adds `Clause` to the dynamic knowledge base. Struct terms are treated as facts; rule terms are inserted as rules. This implementation appends the clause, like `assertz/1`.

        **Examples**

        ```prolog
        ?- assert(p(1)), assert(p(2)).
        yes.

        ?- p(X).
        X = 1 ; X = 2.

        ?- assert((q(X) :- p(X))), q(2).
        yes.

        ?- assert(1).
        throws error(type_error(callable, 1), _).
        ```
        """.trimIndent()
}
