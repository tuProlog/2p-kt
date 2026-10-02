package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Callable : TypeTester<ExecutionContext>("callable") {
    override val help: String =
        """
        `callable(@Term)`
        
        Succeeds if `Term` is callable by the solver, i.e. represented as a structure; fails otherwise.
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is Struct
}
