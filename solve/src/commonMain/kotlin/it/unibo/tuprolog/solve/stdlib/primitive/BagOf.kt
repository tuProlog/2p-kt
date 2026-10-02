package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term

object BagOf : AbstractCollectionOf("bagof") {
    override val help: String =
        """
        `bagof(?Template, +Goal, -Bag)`
        
        Collects instances of `Template` produced by solutions of `Goal`, preserving duplicates and solution order. Free variables of `Goal` induce separate groups unless existentially quantified with `^/2`.
        """.trimIndent()

    override fun processSolutions(list: List<Term>): Iterable<Term> = list
}
