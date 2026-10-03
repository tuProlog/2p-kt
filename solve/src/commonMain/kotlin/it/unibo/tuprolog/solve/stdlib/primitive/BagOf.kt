package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term

object BagOf : AbstractCollectionOf("bagof") {
    override val help: String =
        """
        `bagof(?Template, +Goal, -Bag)`
        
        Collects instances of `Template` produced by solutions of `Goal`, preserving duplicates and solution order. Free variables of `Goal` induce separate groups unless existentially quantified with `^/2`.

        **Examples**

        ```prolog
        ?- bagof(X, member(X, [c, a, c]), L).
        L = [c, a, c].

        ?- bagof(X, member(X-Y, [1-a, 2-b, 3-a]), L).
        Y = a, L = [1, 3] ; Y = b, L = [2].

        ?- bagof(X, Y^member(X-Y, [1-a, 2-b]), L).
        L = [1, 2].

        ?- bagof(X, fail, L).
        no.
        ```
        """.trimIndent()

    override fun processSolutions(list: List<Term>): Iterable<Term> = list
}
