package it.unibo.tuprolog.examples

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.theory.parsing.ClausesReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadEditSolveKt {
    @Test
    fun main() {
        val inputStream = ReadEditSolveKt::class.java.getResourceAsStream("increment.pl")!!
        val clauseReader = ClausesReader.withDefaultOperators()
        val theory = clauseReader.readTheory(inputStream)
        val solver = Solver.prolog.mutableSolverWithDefaultBuiltins(staticKb = theory)
        val fact = Struct.of("diff", Integer.of(15))
        solver.assertZ(fact)
        val query = Struct.of("increment", Integer.of(15), Var.of("X"))
        for (solution in solver.solve(query)) {
            if (solution.isYes) {
                val value = solution.substitution.getByName("X")
                val valueAsBigInteger = value?.asInteger()?.value
                val actualValue = valueAsBigInteger?.toInt()
                assertEquals(30, actualValue)
            }
        }
    }

    @Test
    fun simple() {
        val inputStream = ReadEditSolveKt::class.java.getResourceAsStream("increment.pl")!!
        val clauseReader = ClausesReader.withDefaultOperators()
        val theory = clauseReader.readTheory(inputStream)
        val solver = Solver.prolog.mutableSolverWithDefaultBuiltins(staticKb = theory)
        val fact = Struct.of("diff", Integer.of(15))
        solver.assertZ(fact)
        val query = Struct.of("increment", Integer.of(15), Var.of("X"))
        val solution = solver.solveOnce(query)
        if (solution.isYes) {
            val value = solution.substitution.getByName("X")
            val valueAsBigInteger = value?.asInteger()?.value
            val actualValue = valueAsBigInteger?.toInt()
            assertEquals(30, actualValue)
        }
    }
}
