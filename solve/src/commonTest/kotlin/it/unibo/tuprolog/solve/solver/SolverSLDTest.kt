package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test

/**
 * Test class for [SolverSLD]
 *
 * @author Enrico
 */
internal class SolverSLDTest {

    @Test
    fun firstInformalTest() {
        val goal = Solve.Request(
                Signature("p", 1),
                listOf(Var.of("X")),
                ExecutionContext(
                        Libraries(Library.of(alias = "test", theory = ClauseDatabase.of(
                                Fact.of(Struct.of("p", Atom.of("a"))),
                                Fact.of(Struct.of("p", Atom.of("b"))),
                                Rule.of(Struct.of("p", Atom.of("b")), Truth.fail()),
                                Fact.of(Struct.of("p", Var.of("X"))),
                                Rule.of(Atom.of("p"), Atom.of("a")),
                                Rule.of(Atom.of("p"), Atom.of("c")),
                                Rule.of(Atom.of("p"), Truth.fail())
                        ))),
                        emptyMap(),
                        ClauseDatabase.of(),
                        ClauseDatabase.of()
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

}
