package it.unibo.tuprolog.solve.stdlib.primitive

object Assert : AbstractAssert("", false)
 {
    override val help: String =
        """
        `assert(+Clause)`
        
        Adds `Clause` to the dynamic knowledge base. Struct terms are treated as facts; rule terms are inserted as rules. This implementation appends the clause, like `assertz/1`.
        """.trimIndent()
}
