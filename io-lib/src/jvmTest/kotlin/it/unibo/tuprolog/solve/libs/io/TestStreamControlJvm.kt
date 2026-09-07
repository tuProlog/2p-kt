package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

/**
 * JVM-only counterpart to [TestStreamControl]: exercises `at_end_of_stream/0,1` scenarios that
 * depend on exact stream-exhaustion detection. This can't live in `commonTest` because, on
 * JS/Node, `InputChannel.of(string)` (`InputChannelFromString`) synthesizes a trailing `'\n'`
 * after every line - even an empty one - so streams never report as exhausted the way the JVM's
 * `StringReader`-backed channel does.
 */
class TestStreamControlJvm {
    @Test
    fun testAtEndOfStream0OnEmptyInput() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "")
            val query = atomOf("at_end_of_stream")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
        }
    }

    @Test
    fun testAtEndOfStream1SucceedsAfterConsumingWholeStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "ab")))
            val query =
                "get_char"("mickey", "C1") and
                    ("get_char"("mickey", "C2") and "at_end_of_stream"("mickey"))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("C1" to "a", "C2" to "b")), solutions)
        }
    }
}
