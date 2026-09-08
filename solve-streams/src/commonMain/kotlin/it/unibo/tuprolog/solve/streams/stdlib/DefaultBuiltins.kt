package it.unibo.tuprolog.solve.streams.stdlib

import it.unibo.tuprolog.solve.library.impl.ExtensionLibrary
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Catch
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Not
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw

/**
 * The [it.unibo.tuprolog.solve.library.Library] of built-in predicates the `:solve-streams` engine adds on top
 * of [CommonBuiltins]: this is what [it.unibo.tuprolog.solve.streams.StreamsSolverFactory.defaultBuiltins]
 * returns, and what a solver built via `Solver.streams.solverWithDefaultBuiltins()` is loaded with.
 *
 * These six control-construct predicates need an engine-specific implementation because they interact directly
 * with this engine's `Sequence`-of-states resolution model (choice points, cut scoping, exception propagation):
 * `call/1`, `catch/3`, `','/2` (conjunction), `'!'/0` (cut), `throw/1` and `'\+'/1` (negation as failure). All
 * six are `internal` `PrimitiveWrapper`s under `it.unibo.tuprolog.solve.streams.stdlib.primitive`, so they are
 * not directly reachable from outside this module -- go through this [DefaultBuiltins] library instead.
 */
object DefaultBuiltins : ExtensionLibrary(CommonBuiltins) {
    override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() =
            listOf(
                Call,
                Catch,
                Conjunction,
                Cut,
                Throw,
                Not,
            )
}
