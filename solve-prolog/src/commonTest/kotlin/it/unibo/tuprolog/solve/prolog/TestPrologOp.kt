package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.error.DomainError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestPrologOp {
    @Test
    fun priorityAbove1200IsDomainError() {
        val goal = Struct.of("op", Integer.of(1201), Atom.of("xfx"), Atom.of("foo"))
        val solution = Solver.prolog.solverWithDefaultBuiltins().solveOnce(goal)
        val error = assertIs<DomainError>((solution as? Solution.Halt)?.exception, "$solution")
        assertEquals(DomainError.Expected.OPERATOR_PRIORITY, error.expectedDomain)
        assertEquals(Integer.of(1201), error.culprit)
    }
}
