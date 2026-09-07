package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

/**
 * JVM-only counterpart to [TestGetPeekCharCode]: exercises exact end-of-file detection on an empty
 * standard input. This can't live in `commonTest` because, on JS/Node, `InputChannel.of("")`
 * (`InputChannelFromString`) synthesizes a trailing `'\n'` after every line - even an empty one -
 * so it never actually reports as exhausted, unlike the JVM's `StringReader`-backed channel.
 */
class TestGetPeekCharCodeJvm {
    @Test
    fun testGetChar1AtEndOfFile() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "")
            val query = "get_char"("Char")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("Char" to "end_of_file")), solutions)
        }
    }

    @Test
    fun testGetCode1AtEndOfFileYieldsMinusOne() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "")
            val query = "get_code"("Code")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("Code" to -1)), solutions)
        }
    }
}
