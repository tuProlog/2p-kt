package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.prolog.fsm.State
import it.unibo.tuprolog.solve.prolog.fsm.StateEnd
import it.unibo.tuprolog.solve.prolog.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AbstractPrologSolverSpec :
    FunSpec({
        test("state-machine/preserves-custom-unificator") {
            val solver = DebuggablePrologSolver(theoryInvolvingAllStates)
            assertSame(CustomUnificator, solver.unificator)
            val goal = Struct.of("count", Integer.of(4))
            assertEquals(
                listOf(goal.yes()),
                solver.solveList(goal),
            )
        }

        test("hijackable-iterator/without-hijacking-solves-as-usual") {
            val transitions = mutableListOf<Long>()
            val solver = HijackingSolver(transitions) { _, destination, _ -> destination }
            val goal = Struct.of("p", Integer.of(1))
            assertEquals(listOf(goal.yes()), solver.solveList(goal))
            assertTrue(transitions.isNotEmpty())
        }

        test("hijackable-iterator/hijacking-replaces-the-next-state") {
            val solver =
                HijackingSolver(mutableListOf()) { _, destination, _ ->
                    if (destination is StateEnd && destination.solution.isYes) {
                        destination.copy(solution = Solution.no(destination.solution.query))
                    } else {
                        destination
                    }
                }
            val goal = Struct.of("p", Integer.of(1))
            assertEquals(listOf(goal.no()), solver.solveList(goal))
        }
    })

private object CustomUnificator : Unificator by Unificator.default

private class DebuggablePrologSolver(
    staticKb: Theory,
) : AbstractPrologSolver(
        unificator = CustomUnificator,
        staticKb = staticKb,
        libraries = Runtime.of(DefaultBuiltins),
    ) {
    override fun solutionIterator(
        initialState: State,
        onStateTransition: (State, State, Long) -> Unit,
    ): SolutionIterator =
        SolutionIterator.of(initialState) { src, dst, i ->
            println(src)
            if (dst.isEndState && dst.context.choicePoints == null) {
                println(dst)
            }
            for (state in listOf(src, dst)) {
                assertSame(
                    expected = state.context.unificator,
                    actual = CustomUnificator,
                    message =
                        "Unificator is unexpectedly reset after $i steps while transitioning from\n" +
                            "      source=$src\n" +
                            " destination=$dst",
                )
            }
            onStateTransition(src, dst, i)
        }

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): AbstractPrologSolver = TODO()

    override fun clone(): AbstractPrologSolver = TODO()
}

private val theoryInvolvingAllStates =
    logicProgramming {
        theoryOf(
            rule {
                "countUpTo"(X, X, Z).impliedBy(
                    Z `is` (X + Y),
                )
            },
            rule {
                "countUpTo"(X, Y, Z).impliedBy(
                    write(X),
                    nl,
                    X lowerThan Y,
                    W `is` (X + 1),
                    "countUpTo"(W, Y, Z),
                )
            },
            rule {
                "count"(X).impliedBy(
                    X greaterThan 0,
                    catch("countUpTo"(0, X, `_`), E, write(E) and nl and fail),
                )
            },
            rule { "count"(X).impliedBy(write(X), nl) },
        )
    }

/** Drives resolution with a [MutableSolutionIterator] that lets [hijack] replace each next state. */
private class HijackingSolver(
    private val transitions: MutableList<Long>,
    private val hijack: (State, State, Long) -> State,
) : AbstractPrologSolver(
        staticKb = Theory.of(Fact.of(Struct.of("p", Integer.of(1)))),
        libraries = Runtime.of(DefaultBuiltins),
    ) {
    override fun solutionIterator(
        initialState: State,
        onStateTransition: (State, State, Long) -> Unit,
    ): SolutionIterator =
        MutableSolutionIterator.of(initialState, hijack) { source, destination, index ->
            transitions += index
            onStateTransition(source, destination, index)
        }

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): AbstractPrologSolver = TODO()

    override fun clone(): AbstractPrologSolver = TODO()
}
