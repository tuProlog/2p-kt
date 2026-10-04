package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solver
import kotlin.test.Test
import kotlin.test.assertTrue

class TestPrologVariantOperators {
    private val solver = Solver.prolog.solverWithDefaultBuiltins()

    private fun solveText(goal: String) = solver.solveOnce(Struct.parse(goal, solver.operators))

    @Test
    fun variantComparisonsAreInfixOperators() {
        assertTrue(solveText("f(a, X) =@= f(a, X)").isYes)
        assertTrue(solveText("f(a) \\=@= f(b)").isYes)
        assertTrue(solveText("X = (a =@= b), X =.. ['=@=', a, b]").isYes)
    }
}
