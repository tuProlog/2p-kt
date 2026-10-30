package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals

private val theory = Theory.of(Fact.of(Struct.of("p", Struct.of("a"))))

class ConcurrentSolverApiSpec :
    FunSpec({
        test("copy/replaces-only-what-it-is-given") {
            val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins()
            val copy = solver.copy(staticKb = theory)
            assertEquals(theory.clauses.toList(), copy.staticKb.clauses.toList())
            assertEquals(solver.libraries, copy.libraries)
        }

        test("clone/keeps-the-knowledge-base") {
            val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
            assertEquals(
                theory.clauses.toList(),
                solver
                    .clone()
                    .staticKb.clauses
                    .toList(),
            )
        }
    })
