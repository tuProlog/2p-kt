package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.Unificator

/** A named way of building a [Unificator] out of a context, so that specs can run against several engines. */
internal class EngineCase(
    val id: String,
    val factory: (Substitution) -> Unificator,
) {
    operator fun invoke(context: Substitution = Substitution.empty()): Unificator = factory(context)

    companion object {
        val strict =
            listOf(
                EngineCase("worklist", WorklistUnificators::strict),
                EngineCase("equational", ReferenceUnificators::strict),
            )
    }
}
