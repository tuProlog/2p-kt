package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Internal documentation relation. The public entry point is `help/2`.
 *
 * Subjects are predicate/function/operator indicators, `flag(Name)`, or `library(Alias)`.
 */
object InternalHelp : BinaryRelation.WithoutSideEffects<ExecutionContext>("__help__") {
    override val help: String = ""

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
    ): Sequence<Substitution> =
        helpEntries()
            .map { (subject, help) -> mgu(first, subject) + mgu(second, Atom.of(help)) }
            .filter { it.isSuccess }

    private fun Solve.Request<ExecutionContext>.helpEntries(): Sequence<Pair<Term, String>> =
        sequence {
            context.libraries.libraries
                .sortedBy { it.alias }
                .forEach { library ->
                    library.help.takeIf { it.isNotBlank() }?.let {
                        yield(Struct.of("library", Atom.of(library.alias)) to it)
                    }
                }

            context.libraries.documentation.entries
                .sortedWith(compareBy({ it.key.name }, { it.key.arity }))
                .forEach { (signature, help) ->
                    yield(signature.toIndicator() to help)
                }

            context.flags.keys
                .sorted()
                .mapNotNull(NotableFlag::fromName)
                .forEach { flag ->
                    yield(Struct.of("flag", Atom.of(flag.name)) to flag.help)
                }
        }
}
