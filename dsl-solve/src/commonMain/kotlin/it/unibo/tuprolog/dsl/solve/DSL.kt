@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Entry point of the resolution-aware Prolog DSL: builds a fresh [LogicProgrammingScope] backed by a
 * [it.unibo.tuprolog.solve.MutableSolver] obtained from [solverFactory] (see
 * [LogicProgrammingScopeWithResolution.defaultSolver]), and runs [function] against it as the receiver. Everything
 * built or asserted inside [function] (facts, rules, queries via `solve`/`staticKb`/`dynamicKb`) shares that one
 * scope and that one underlying solver.
 *
 * ```kotlin
 * logicProgramming(SolverFactory) {
 *     staticKb(fact { "parent"("abraham", "isaac") })
 *     solve("parent"("abraham", "X")).forEach { println(it) }
 * }
 * ```
 *
 * @param solverFactory the [SolverFactory] used both to build the scope's default solver and, unless overridden,
 * as the source of [unificator].
 * @param unificator the [Unificator] shared by the scope's term-building helpers and its default solver; must be
 * compatible with [solverFactory] (see [LogicProgrammingScope.of]).
 */
@JsName("logicProgramming")
fun <R> logicProgramming(
    solverFactory: SolverFactory,
    unificator: Unificator = solverFactory.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = LogicProgrammingScope.of(solverFactory, unificator).function()

/** Shorthand alias for [logicProgramming]. */
@JsName("lp")
fun <R> lp(
    solverFactory: SolverFactory,
    unificator: Unificator = solverFactory.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(solverFactory, unificator, function)

/**
 * Shorthand for [logicProgramming] defaulting its `solverFactory` argument to [Solver.prolog], the classic,
 * ISO-standard SLD-NF resolution engine (`:solve-classic`). This is the DSL's main entry point for everyday use:
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
 * Requires a concrete solver implementation (e.g. `:solve-classic`) on the classpath, since `Solver.prolog` resolves
 * to it lazily at runtime.
 *
 * @param unificator the [Unificator] shared by the scope's term-building helpers and its default solver.
 */
@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = Solver.prolog.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(Solver.prolog, unificator, function)
