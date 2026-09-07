package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

/**
 * JVM-only counterpart to [TestGetPeekCharCode]. Building the expected `-1` substitution through
 * the DSL hits an unrelated, pre-existing bug on JS: `it.unibo.tuprolog.utils.NumberTypeTester`
 * classifies a number as an integer via the regex `"[0-9]+"`, which doesn't match a leading `-`, so
 * `-1` is misclassified as a real number (`Code` ends up bound to `-1.0`, not `-1`). That's not one
 * of the two `:solve` channel/store bugs this file's sibling tests exist for, so it's left as a
 * JVM-only regression test rather than fixed here.
 */
class TestGetPeekCharCodeJvm {
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
