package it.unibo.tuprolog.solve.stdlib.primitive

object AssertA : AbstractAssert("a", true) {
    override val help: String =
        """
        `asserta(+Clause)`
        
        Adds `Clause` to the beginning of the corresponding dynamic predicate. Struct terms are treated as facts.
        """.trimIndent()
}
