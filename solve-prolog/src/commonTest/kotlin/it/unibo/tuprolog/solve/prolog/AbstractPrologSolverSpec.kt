package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.unify.Unificator

class AbstractPrologSolverSpec :
    FunSpec({
        test("machine/preserves-custom-unificator") {
            val solver =
                PrologSolverFactory.solverWithDefaultBuiltins(
                    unificator = CustomUnificator,
                    staticKb = theoryInvolvingAllStates,
                )
            solver.unificator shouldBeSameInstanceAs CustomUnificator
            val goal = Struct.of("count", Integer.of(4))
            solver.solveList(goal) shouldBe listOf(goal.yes())
            // the machine reports each new world to the solver: the unificator must survive the resolution
            solver.unificator shouldBeSameInstanceAs CustomUnificator
        }
    })

private object CustomUnificator : Unificator by Unificator.default

private val theoryInvolvingAllStates =
    logicProgramming {
        theoryOf(
            rule {
                "countUpTo"(X, X, Z).impliedBy(
                    Z `is` (X + Y),
                )
            },
            rule {
                "countUpTo"(X, Y, Z).impliedBy(
                    write(X),
                    nl,
                    X lowerThan Y,
                    W `is` (X + 1),
                    "countUpTo"(W, Y, Z),
                )
            },
            rule {
                "count"(X).impliedBy(
                    X greaterThan 0,
                    catch("countUpTo"(0, X, `_`), E, write(E) and nl and fail),
                )
            },
            rule { "count"(X).impliedBy(write(X), nl) },
        )
    }
