package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.theory.Theory

private val theory = Theory.of(Fact.of(Struct.of("p", Struct.of("a"))))

class ConcurrentSolverApiSpec :
    FunSpec({
        test("copy/replaces-only-what-it-is-given") {
            val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins()
            val copy = solver.copy(staticKb = theory)
            copy.staticKb.clauses.toList() shouldBe theory.clauses.toList()
            copy.libraries shouldBe solver.libraries
        }

        test("clone/keeps-the-knowledge-base") {
            val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
            (
                solver
                    .clone()
                    .staticKb.clauses
                    .toList()
            ) shouldBe theory.clauses.toList()
        }
    })
