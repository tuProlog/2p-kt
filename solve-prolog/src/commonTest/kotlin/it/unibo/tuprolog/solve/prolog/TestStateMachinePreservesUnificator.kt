package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.fsm.State
import it.unibo.tuprolog.solve.prolog.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TestStateMachinePreservesUnificator {
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

    @Test
    fun testUnificatorIsPreserved() {
        val solver = DebuggablePrologSolver(theoryInvolvingAllStates)
        assertSame(CustomUnificator, solver.unificator)
        val goal = Struct.of("count", Integer.of(4))
        assertEquals(
            listOf(goal.yes()),
            solver.solveList(goal),
        )
    }
}
