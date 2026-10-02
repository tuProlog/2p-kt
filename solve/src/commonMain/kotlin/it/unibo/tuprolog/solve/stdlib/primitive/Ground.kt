package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Ground : TypeTester<ExecutionContext>("ground") {
    override val help: String =
        """
        `ground(@Term)`
        
        Succeeds if `Term` contains no variables; fails otherwise.
        """.trimIndent()

    override fun testType(term: Term): Boolean = term.isGround
}
