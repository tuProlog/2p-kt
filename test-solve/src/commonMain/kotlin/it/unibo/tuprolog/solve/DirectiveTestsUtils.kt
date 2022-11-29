package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.theory.Theory

object DirectiveTestsUtils {

    private const val BIG_THEORY_SIZE = 40000

    fun bigTheory(size: Int = BIG_THEORY_SIZE, last: (LogicProgrammingScope.() -> Clause)? = null) =
        logicProgramming {
            theoryOf(
                sequence {
                    for (i in 1..size) {
                        yield(fact { "f$i" })
                    }
                    if (last != null) {
                        yield(this@logicProgramming.last())
                    }
                }
            )
        }

    fun dynamicDirective(functor: String, arity: Int): Sequence<Directive> =
        logicProgramming {
            sequenceOf(
                directive { "dynamic"(functor / arity) }
            )
        }

    fun staticDirective(functor: String, arity: Int): Sequence<Directive> =
        logicProgramming {
            sequenceOf(
                directive { "static"(functor / arity) }
            )
        }

    fun facts(functor: String, iterable: Iterable<Any>): Sequence<Fact> =
        logicProgramming {
            iterable.asSequence().map { fact { functor(it) } }
        }

    fun solverInitializers(solverFactory: SolverFactory): List<(Theory) -> Solver> =
        listOf(
            { solverFactory.solverOf(staticKb = it) },
            { solverFactory.solverOf(dynamicKb = it.toMutableTheory()) },
            { solverFactory.mutableSolverOf().also { s -> s.loadStaticKb(it) } },
            { solverFactory.mutableSolverOf().also { s -> s.loadDynamicKb(it.toMutableTheory()) } }
        )

    fun solverInitializersWithEventsList(
        solverFactory: SolverFactory
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
                        warnings = warn
                    )
                } to event
            },
            stdOut { event, out, warn ->
                { t: Theory ->
                    solverFactory.mutableSolverWithDefaultBuiltins(stdOut = out, stdErr = out, warnings = warn)
                        .also { it.loadStaticKb(t) }
                } to event
            },
            stdOut { event, out, warn ->
                { t: Theory ->
                    solverFactory.mutableSolverWithDefaultBuiltins(stdOut = out, stdErr = out, warnings = warn)
                        .also { it.loadDynamicKb(t.toMutableTheory()) }
                } to event
            }
        )
    }
}
