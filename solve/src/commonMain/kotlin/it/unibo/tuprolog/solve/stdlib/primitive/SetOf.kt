package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term

object SetOf : AbstractCollectionOf("setof") {
    override val help: String =
        """
        `setof(?Template, +Goal, -Set)`
        
        Collects instances of `Template` produced by solutions of `Goal`, removing duplicate values while preserving first-occurrence order. Free variables of `Goal` induce separate groups unless existentially quantified with `^/2`.

        **Examples**

        ```prolog
        % duplicates are removed, first occurrences keep their order
        ?- setof(X, member(X, [c, a, b, a]), S).
        S = [c, a, b].

        ?- setof(X, Y^member(X-Y, [1-a, 2-b, 1-c]), S).
        S = [1, 2].

        ?- setof(X, member(X-Y, [1-a, 2-b, 3-a]), S).
        Y = a, S = [1, 3] ; Y = b, S = [2].

        ?- setof(X, fail, S).
        no.
        ```
        """.trimIndent()

    override fun processSolutions(list: List<Term>): Iterable<Term> = LinkedHashSet(list)
}
