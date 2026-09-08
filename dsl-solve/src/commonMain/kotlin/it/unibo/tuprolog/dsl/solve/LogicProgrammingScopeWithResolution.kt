package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnificator
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * Adds resolution-driving sugar to [LogicProgrammingScopeWithUnificator]: implements [MutableSolver] itself
 * (forwarding to [defaultSolver], e.g. via `MutableSolver by defaultSolver` as [LogicProgrammingScopeImpl] does), so
 * `solve`/`solveOnce`/`solveList` and every knowledge-base-mutating operation
 * ([MutableSolver.assertZ], [MutableSolver.retract], ...) are callable directly on the scope — plus
 * [staticKb]/[dynamicKb] shorthands for loading a whole knowledge base at once, and [solverOf] for building
 * additional, independent solvers sharing this scope's [solverFactory]/[unificator].
 *
 * This is what turns the DSL from a pure term/theory builder into something that can actually run queries:
 * ```kotlin
 * prolog {
 *     staticKb(
 *         fact { "parent"("abraham", "isaac") },
 *         rule { "ancestor"("X", "Y") `if` "parent"("X", "Y") },
 *     )
 *     for (solution in solve("ancestor"("abraham", "X"))) {
 *         if (solution is it.unibo.tuprolog.solve.Solution.Yes) {
 *             println(solution.substitution["X"])
 *         }
 *     }
 * }
 * ```
 * `solve` (and the rest of the [MutableSolver]/[it.unibo.tuprolog.solve.Solver] surface) is inherited, undocumented
 * here, from [it.unibo.tuprolog.solve.Solver] and [MutableSolver] themselves.
 *
 * @param S the concrete, self-referential scope type (see [it.unibo.tuprolog.dsl.BaseLogicProgrammingScope]).
 */
interface LogicProgrammingScopeWithResolution<S : LogicProgrammingScopeWithResolution<S>> :
    LogicProgrammingScopeWithUnificator<S>,
    MutableSolver {
    /** The [SolverFactory] this scope's [defaultSolver] was created from, and that [solverOf] builds further solvers from by default. */
    @JsName("solverFactory")
    val solverFactory: SolverFactory

    /**
     * The [MutableSolver] this scope forwards [MutableSolver]/[it.unibo.tuprolog.solve.Solver] operations
     * (`solve`, [staticKb], [dynamicKb], `assertZ`, ...) to. Built once when the scope is created (see
     * [LogicProgrammingScopeImpl]'s secondary constructor) via [SolverFactory.mutableSolverOf].
     */
    @JsName("defaultSolver")
    val defaultSolver: MutableSolver

    /**
     * Builds a brand-new [MutableSolver] from [solverFactory] (defaulting every argument to [solverFactory]'s
     * `default*` values, except [unificator] which defaults to this scope's own), including [solverFactory]'s
     * standard-library builtins — see [SolverFactory.mutableSolverWithDefaultBuiltins]. Unlike [defaultSolver],
     * this returns an independent solver each time it's called, not the one backing this scope's `solve`/`staticKb`
     * calls.
     */
    @JsName("solverOf")
    fun solverOf(
        unificator: Unificator = this.unificator,
        otherLibraries: Runtime = solverFactory.defaultRuntime,
        flags: FlagStore = solverFactory.defaultFlags,
        staticKb: Theory = solverFactory.defaultStaticKb,
        dynamicKb: Theory = solverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = solverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = solverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = solverFactory.defaultErrorChannel,
        warnings: OutputChannel<Warning> = solverFactory.defaultWarningsChannel,
    ): MutableSolver =
        solverFactory.mutableSolverWithDefaultBuiltins(
            unificator,
            otherLibraries,
            flags,
            staticKb,
            dynamicKb,
            stdIn,
            stdOut,
            stdErr,
            warnings,
        )

    /** Replaces [defaultSolver]'s static knowledge base with a [Theory] indexing [clauses]; see [MutableSolver.loadStaticClauses]. */
    @JsName("staticKbByArray")
    fun staticKb(vararg clauses: Clause) = defaultSolver.loadStaticClauses(*clauses)

    /** Replaces [defaultSolver]'s static knowledge base with a [Theory] indexing [clauses]; see [MutableSolver.loadStaticClauses]. */
    @JsName("staticKbByIterable")
    fun staticKb(clauses: Iterable<Clause>) = defaultSolver.loadStaticClauses(clauses)

    /** Replaces [defaultSolver]'s static knowledge base with a [Theory] indexing [clauses]; see [MutableSolver.loadStaticClauses]. */
    @JsName("staticKbBySequence")
    fun staticKb(clauses: Sequence<Clause>) = defaultSolver.loadStaticClauses(clauses)

    /** Replaces [defaultSolver]'s static knowledge base with [theory]; see [MutableSolver.loadStaticKb]. */
    @JsName("staticKbByTheory")
    fun staticKb(theory: Theory) = defaultSolver.loadStaticKb(theory)

    /** Replaces [defaultSolver]'s dynamic knowledge base with a [Theory] indexing [clauses]; see [MutableSolver.loadDynamicClauses]. */
    @JsName("dynamicKbByArray")
    fun dynamicKb(vararg clauses: Clause) = defaultSolver.loadDynamicClauses(*clauses)

    /** Replaces [defaultSolver]'s dynamic knowledge base with a [Theory] indexing [clauses]; see [MutableSolver.loadDynamicClauses]. */
    @JsName("dynamicKbByIterable")
    fun dynamicKb(clauses: Iterable<Clause>) = defaultSolver.loadDynamicClauses(clauses)

    /** Replaces [defaultSolver]'s dynamic knowledge base with a [Theory] indexing [clauses]; see [MutableSolver.loadDynamicClauses]. */
    @JsName("dynamicKbBySequence")
    fun dynamicKb(clauses: Sequence<Clause>) = defaultSolver.loadDynamicClauses(clauses)

    /** Replaces [defaultSolver]'s dynamic knowledge base with [theory]; see [MutableSolver.loadDynamicKb]. */
    @JsName("dynamicKbByTheory")
    fun dynamicKb(theory: Theory) = defaultSolver.loadDynamicKb(theory)
}
