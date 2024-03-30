package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.theory.Theory

object TimeRelatedTheories {
    internal val timeOutException = TimeOutException(context = DummyInstances.executionContext, exceededDuration = 1)

    /**
     * Clause database used for time-related tests:
     *
     * ```prolog
     * a(X) :- b(X).
     * b(500) :- sleep(500).
     * b(600) :- sleep(600).
     * b(700) :- sleep(700).
     * ```
     */
    val timeRelatedTheory: Theory by lazy {
        logicProgramming {
            theoryOf(
                rule { "a"("X") `if` "b"("X") },
                rule { "b"(500) `if` "sleep"(500) },
                rule { "b"(600) `if` "sleep"(600) },
                rule { "b"(700) `if` "sleep"(700) },
            )
        }
    }

    /**
     * Notable request goal over [timeRelatedTheory] and respective expected [Solution]s.
     * In particular the requested goal assumes the resolution terminates in less than 500 ms,
     * thus a [TimeOutException] is returned as the first solution
     */
    val lessThan500MsGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "a"("X").hasSolutions(
                    { halt(timeOutException) },
                ),
            )
        }
    }

    /**
     * Notable request goal over [timeRelatedTheory] and respective expected [Solution]s.
     * In particular the requested goal assumes the resolution terminates in slightly more than 500 ms
     * (and, in any case, within 600 ms),
     * thus a [TimeOutException] is returned as the second solution
     */
    val slightlyMoreThan500MsGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "a"("X").hasSolutions(
                    { yes("X" to 500) },
                    { halt(timeOutException) },
                ),
            )
        }
    }

    /**
     * Notable request goal over [timeRelatedTheory] and respective expected [Solution]s.
     * In particular the requested goal assumes the resolution terminates in slightly more than 600 ms
     * (and, in any case, within 700 ms),
     * thus a [TimeOutException] is returned as the third solution
     */
    val slightlyMoreThan600MsGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "a"("X").hasSolutions(
                    { yes("X" to 500) },
                    { yes("X" to 600) },
                    { halt(timeOutException) },
                ),
            )
        }
    }

    /**
     * Notable request goal over [timeRelatedTheory] and respective expected [Solution]s.
     * In particular the requested goal assumes the resolution terminates in slightly more than 700 ms,
     * thus no [TimeOutException] is returned and 3 positive solutions are returned instead
     */
    val slightlyMoreThan700MsGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "a"("X").hasSolutions(
                    { yes("X" to 500) },
                    { yes("X" to 600) },
                    { yes("X" to 700) },
                ),
            )
        }
    }
}
