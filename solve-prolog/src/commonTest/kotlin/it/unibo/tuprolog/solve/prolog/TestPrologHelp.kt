package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solver
import kotlin.test.Test
import kotlin.test.assertTrue

class TestPrologHelp {
    private val solver = Solver.prolog.solverWithDefaultBuiltins()

    @Test
    fun helpDocumentsBuiltins() {
        val help = Var.of("Help")
        val solution = solver.solveOnce(Struct.of("help", Signature("functor", 3).toIndicator(), help))
        assertTrue(solution.isYes)
    }

    @Test
    fun helpDocumentsNotableFlags() {
        val help = Var.of("Help")
        val solution = solver.solveOnce(Struct.of("help", Struct.of("flag", Atom.of("unknown")), help))
        assertTrue(solution.isYes)
    }

    @Test
    fun helpCanEnumerateFlags() {
        val name = Var.of("Name")
        val help = Var.of("Help")
        val solutions = solver.solve(Struct.of("help", Struct.of("flag", name), help))
        assertTrue(solutions.any { it.isYes })
    }
}
