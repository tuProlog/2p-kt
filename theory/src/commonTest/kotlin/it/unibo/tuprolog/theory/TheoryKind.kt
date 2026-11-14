package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

/** A kind of indexed theory, built out of clauses, to run the same checks against each kind. */
internal class TheoryKind(
    val id: String,
    val of: (List<Clause>) -> Theory,
) {
    companion object {
        val all =
            listOf(
                TheoryKind("frozen") { Theory.indexedOf(Unificator.default, it) },
                TheoryKind("mutable") { MutableTheory.indexedOf(Unificator.default, it) },
            )
    }
}
