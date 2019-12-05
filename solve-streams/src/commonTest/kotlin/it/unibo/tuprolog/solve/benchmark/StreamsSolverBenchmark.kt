package it.unibo.tuprolog.solve.benchmark

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Benchmark class for [StreamsSolver] implementation
 *
 * @author Enrico
 */
internal class StreamsSolverBenchmark : SolverFactory {

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase
    ): Solver = StreamsSolver(libraries, flags, staticKB, dynamicKB)

    @Test
    @Ignore
    fun `8-QueensBenchmark`() {
        val queensDB = prolog {
            theory(
                { "top" `if` ("queens"(8, "Qs") and false) },
                { "top" },

                {
                    "queens"("N", "Qs") `if` (
                            "range"(1, "N", "Ns") and
                                    "queens"("Ns", "[]", "Qs")
                            )
                },

                { "queens"("[]", "Qs", "Qs") },
                {
                    "queens"("UnplacedQs", "SafeQs", "Qs") `if` (
                            "select"("UnplacedQs", "UnplacedQs1", "Q") and
                                    "not_attack"("SafeQs", "Q") and
                                    "queens"(
                                        "UnplacedQs1",
                                        listFrom(kotlin.collections.listOf(varOf("Q")), varOf("SafeQs")),
                                        "Qs"
                                    )
                            )
                },

                { "not_attack"("Xs", "X") `if` "not_attack"("Xs", "X", 1) },

                { "not_attack"("[]", `_`, `_`) `if` "!" },
                {
                    "not_attack"(listFrom(kotlin.collections.listOf(varOf("Y")), varOf("Ys")), "X", "N") `if` (
                            "=\\="("X", varOf("Y") + "N") and
                                    "=\\="("X", varOf("Y") - "N") and
                                    ("N1" `is` (varOf("N") + 1)) and
                                    "not_attack"("Ys", "X", "N1")
                            )
                },

                { "select"(listFrom(kotlin.collections.listOf(varOf("X")), varOf("Xs")), "Xs", "X") },
                {
                    "select"(
                        listFrom(kotlin.collections.listOf(varOf("Y")), varOf("Ys")),
                        listFrom(kotlin.collections.listOf(varOf("Y")), varOf("Zs")),
                        "X"
                    ) `if` "select"("Ys", "Zs", "X")
                },

                { ("range"("N", "N", listOf("N")) `if` "!") },
                {
                    ("range"("M", "N", listFrom(kotlin.collections.listOf(varOf("M")), varOf("Ns"))) `if` (
                            "<"("M", "N") and
                                    ("M1" `is` (varOf("M") + 1)) and
                                    "range"("M1", "N", "Ns")
                            ))
                }
            )
        }

        val numberOfExecutions = 10 // it will take approximately 3m 40s
        val timeList = mutableListOf<Long>()

        var solver: Solver
        var initialTime: TimeInstant

        repeat(numberOfExecutions) {
            println(it)
            solver = solverOf(staticKB = queensDB)

            initialTime = currentTimeInstant()
            solver.solve { atomOf("top") }.toList()
            timeList.add(currentTimeInstant() - initialTime)
        }

        println("\n8-Queens: ${timeList.sum() / timeList.size} ms to solve in an average of $numberOfExecutions executions")
        println(timeList)
    }

}
