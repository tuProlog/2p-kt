package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import kotlin.test.Test
import kotlin.test.assertIs
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

    @Test
    fun operatorHelpCombinesSemanticsAndSyntax() {
        val addition = helpFor(Signature("+", 2).toIndicator())
        assertTrue("arithmetic sum" in addition)
        assertTrue("Operator `+`" in addition)
        assertTrue("`yfx`" in addition)
        assertTrue("priority `500`" in addition)

        val arithmeticEquality = helpFor(Signature("=:=", 2).toIndicator())
        assertTrue("numerically equal" in arithmeticEquality)
        assertTrue("`xfx`" in arithmeticEquality)
        assertTrue("priority `700`" in arithmeticEquality)

        val disjunction = helpFor(Signature(";", 2).toIndicator())
        assertTrue("disjunction" in disjunction)
        assertTrue("`xfy`" in disjunction)
        assertTrue("priority `1100`" in disjunction)
    }

    @Test
    fun semanticHelpReplacesGeneratedFallbacks() {
        val subjects =
            listOf(
                Signature("functor", 3).toIndicator(),
                Signature("+", 2).toIndicator(),
                Signature("member", 2).toIndicator(),
            )
        for (subject in subjects) {
            val help = helpFor(subject)
            assertTrue("\n\nPrimitive." !in help)
            assertTrue("\n\nFunction." !in help)
            assertTrue("\n\nRule." !in help)
        }
    }

    private fun helpFor(subject: it.unibo.tuprolog.core.Term): String {
        val help = Var.of("Help")
        val solution = assertIs<Solution.Yes>(solver.solveOnce(Struct.of("help", subject, help)))
        return assertIs<Atom>(solution.substitution[help]).value
    }
}
