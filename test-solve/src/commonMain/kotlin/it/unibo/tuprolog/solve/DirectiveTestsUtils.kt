package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.theory.Theory

/**
 * Shared helpers for [TestDirectives], covering directive-related behavior (`dynamic/1`, `static/1`, `initialization/1`,
 * `solve/1`, flag/operator-setting directives, and how a `Solver`/`MutableSolver` reacts to loading a theory built
 * from them) that would otherwise require boilerplate repeated across every test case.
 */
object DirectiveTestsUtils {
    private const val BIG_THEORY_SIZE = 40000

    /**
     * Builds a theory of [size] facts `f1.`, `f2.`, ..., optionally followed by one [last] clause; used by
     * [TestDirectives.testDirectiveLoadingQuickly] to check that loading a large static knowledge base does not
     * incur pathological (e.g. quadratic) loading time.
     */
    fun bigTheory(
        size: Int = BIG_THEORY_SIZE,
        last: (LogicProgrammingScope.() -> Clause)? = null,
    ) = logicProgramming {
        theoryOf(
            sequence {
                for (i in 1..size) {
                    yield(fact { "f$i" })
                }
                if (last != null) {
                    yield(this@logicProgramming.last())
                }
            },
        )
    }

    /** A single-clause sequence containing the directive `dynamic(functor/arity).`. */
    fun dynamicDirective(
        functor: String,
        arity: Int,
    ): Sequence<Directive> =
        logicProgramming {
            sequenceOf(
                directive { "dynamic"(functor / arity) },
            )
        }

    /** A single-clause sequence containing the directive `static(functor/arity).`. */
    fun staticDirective(
        functor: String,
        arity: Int,
    ): Sequence<Directive> =
        logicProgramming {
            sequenceOf(
                directive { "static"(functor / arity) },
            )
        }

    /** A sequence of facts `functor(e).`, one for each element `e` of [iterable]. */
    fun facts(
        functor: String,
        iterable: Iterable<Any>,
    ): Sequence<Fact> =
        logicProgramming {
            iterable.asSequence().map { fact { functor(it) } }
        }

    /**
     * The four equivalent ways a [given][Theory] can end up as a solver's knowledge base: as the static or dynamic
     * theory passed to [SolverFactory.solverOf], or loaded after the fact into a fresh [MutableSolver] via
     * [MutableSolver.loadStaticKb]/[MutableSolver.loadDynamicKb]. [TestDirectives] runs its directive-loading
     * assertions against every one of these, since a directive (e.g. `dynamic/1`) must behave the same way
     * regardless of how its enclosing theory was loaded.
     */
    fun solverInitializers(solverFactory: SolverFactory): List<(Theory) -> Solver> =
        listOf(
            { solverFactory.solverOf(staticKb = it) },
            { solverFactory.solverOf(dynamicKb = it.toMutableTheory()) },
            { solverFactory.mutableSolverOf().also { s -> s.loadStaticKb(it) } },
            { solverFactory.mutableSolverOf().also { s -> s.loadDynamicKb(it.toMutableTheory()) } },
        )

    /**
     * Same four loading strategies as [solverInitializers], but built with default built-ins and each paired with
     * the mutable event list its solver's `stdOut`/`stdErr`/`warnings` channels append to — so a caller can load a
     * theory containing `initialization/1`/`solve/1`/writing directives and then assert on what was written or
     * warned about during loading.
     */
    fun solverInitializersWithEventsList(
        solverFactory: SolverFactory,
    ): List<Pair<(Theory) -> Solver, MutableList<Any>>> {
        fun <R> stdOut(action: (MutableList<Any>, OutputChannel<String>, OutputChannel<Warning>) -> R): R {
            val events = mutableListOf<Any>()
            val outputChannel = OutputChannel.of<String> { events.add(it) }
            val warningChannel = OutputChannel.of<Warning> { events.add(it) }
            return action(events, outputChannel, warningChannel)
        }
        return listOf(
            stdOut { event, out, warn ->
                { t: Theory ->
                    solverFactory.solverWithDefaultBuiltins(staticKb = t, stdOut = out, stdErr = out, warnings = warn)
                } to event
            },
            stdOut { event, out, warn ->
                { t: Theory ->
                    solverFactory.solverWithDefaultBuiltins(
                        dynamicKb = t.toMutableTheory(),
                        stdOut = out,
                        stdErr = out,
                        warnings = warn,
                    )
                } to event
            },
            stdOut { event, out, warn ->
                { t: Theory ->
                    solverFactory
                        .mutableSolverWithDefaultBuiltins(stdOut = out, stdErr = out, warnings = warn)
                        .also { it.loadStaticKb(t) }
                } to event
            },
            stdOut { event, out, warn ->
                { t: Theory ->
                    solverFactory
                        .mutableSolverWithDefaultBuiltins(stdOut = out, stdErr = out, warnings = warn)
                        .also { it.loadDynamicKb(t.toMutableTheory()) }
                } to event
            },
        )
    }
}
