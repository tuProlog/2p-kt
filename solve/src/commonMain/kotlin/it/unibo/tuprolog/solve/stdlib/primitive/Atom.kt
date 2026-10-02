package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester

object Atom : TypeTester<ExecutionContext>("atom") {
    override val help: String =
        """
        `atom(@Term)`
        
        Succeeds if `Term` is an atom; fails otherwise. The predicate does not instantiate its argument.
        """.trimIndent()

    override fun testType(term: Term): Boolean = term is it.unibo.tuprolog.core.Atom
}
