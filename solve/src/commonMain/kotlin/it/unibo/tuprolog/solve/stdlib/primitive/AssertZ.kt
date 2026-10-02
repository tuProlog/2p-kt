package it.unibo.tuprolog.solve.stdlib.primitive

object AssertZ : AbstractAssert("z", false)
 {
    override val help: String =
        """
        `assertz(+Clause)`
        
        Adds `Clause` to the end of the corresponding dynamic predicate. Struct terms are treated as facts.
        """.trimIndent()
}
