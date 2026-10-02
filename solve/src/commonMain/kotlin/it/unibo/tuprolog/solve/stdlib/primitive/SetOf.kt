package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term

object SetOf : AbstractCollectionOf("setof") {
    override val help: String =
        """
        `setof(?Template, +Goal, -Set)`
        
        Collects instances of `Template` produced by solutions of `Goal`, removing duplicate values while preserving first-occurrence order. Free variables of `Goal` induce separate groups unless existentially quantified with `^/2`.
        """.trimIndent()

    override fun processSolutions(list: List<Term>): Iterable<Term> = LinkedHashSet(list)
}
