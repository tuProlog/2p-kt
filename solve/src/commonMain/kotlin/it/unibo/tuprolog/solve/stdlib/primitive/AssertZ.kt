package it.unibo.tuprolog.solve.stdlib.primitive

object AssertZ : AbstractAssert("z", false) {
    override val help: String =
        """
        `assertz(+Clause)`
        
        Adds `Clause` to the end of the corresponding dynamic predicate. Struct terms are treated as facts.

        **Examples**

        ```prolog
        ?- assertz(p(1)), assertz(p(2)).
        yes.

        ?- p(X).
        X = 1 ; X = 2.

        ?- assertz(X).
        throws error(instantiation_error, _).
        ```
        """.trimIndent()
}
