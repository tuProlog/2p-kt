package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The [SolverFactory] for `:solve-concurrent`, backing [it.unibo.tuprolog.solve.Solver.concurrent]: every
 * [Solver]/[MutableSolver] it produces is a [ConcurrentSolver] that resolves goals by running the alternatives of
 * the search tree (matching clauses, disjunction branches, backtracking primitive solutions) as concurrent Kotlin
 * coroutines rather than one at a time -- see [ConcurrentSolver] for the resulting caveats around solution
 * ordering and concurrent knowledge-base mutation.
 *
 * ```kotlin
 * val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
 * val solution = solver.solveOnce(query)
 * ```
 *
 * @see ConcurrentSolver
 * @see it.unibo.tuprolog.solve.Solver.concurrent
 */
object ConcurrentSolverFactory : SolverFactory {
    /** The standard-library predicates and rules of `:solve-concurrent` (see `it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins`). */
    override val defaultBuiltins: Library
        get() = DefaultBuiltins

    override fun solverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): Solver = ConcurrentSolverImpl(unificator, libraries, flags, staticKb, dynamicKb, inputs, outputs)

    override fun solverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): Solver = ConcurrentSolverImpl(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): MutableSolver =
        MutableConcurrentSolver(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): MutableSolver = MutableConcurrentSolver(unificator, libraries, flags, staticKb, dynamicKb, inputs, outputs)
}
