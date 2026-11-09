package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.exception.warning.MissingPredicate
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown

/**
 * A context that outlives the moment it was made for (here, a warning's, read once the resolution is over) shows the
 * bindings of that moment, not the later ones (#1010).
 */
class PinnedContextsSpec :
    FunSpec({
        withTests(nameFn = { "${it.first}/warning-context-keeps-its-bindings" }, engines) { (_, factory) ->
            val warnings = mutableListOf<Warning>()
            val solver =
                factory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to Unknown.WARNING),
                    warnings = OutputChannel.of { warnings.add(it) },
                )
            val query = Struct.parse("(X = 1, missing(X) ; X = 2)")
            val x = query.variables.first { it.name == "X" }

            solver.solveList(query).single().substitution[x] shouldBe Integer.of(2)
            val warning = warnings.single().shouldBeInstanceOf<MissingPredicate>()
            warning.context.substitution[x] shouldBe Integer.of(1)
        }
    })
