package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.solve.yes

/**
 * `get_code/1` and `peek_code/1` on a character beyond the Basic Multilingual Plane, which a JVM
 * [java.io.Reader]-backed channel reads whole: character codes are UTF-16 `Char`s, so it is a
 * `representation_error(character_code)`, rather than the code of its high surrogate with the low one silently lost.
 */
class AstralCharacterCodeSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        fun error(name: String) =
            RepresentationError.of(ctx, Signature(name, 1), RepresentationError.Limit.CHARACTER_CODE)

        test("get-code-on-an-astral-character-is-a-representation-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "😀b")
                val query = "get_code"("C")
                assertSolutionEquals(listOf(query.halt(error("get_code"))), solver.solve(query).toList())
            }
        }

        test("get-code-consumes-the-astral-character-whole") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "😀b")
                val query = "catch"("get_code"("_"), "_", true) and "get_code"("C")
                assertSolutionEquals(listOf(query.yes("C" to 'b'.code)), solver.solve(query).toList())
            }
        }

        test("peek-code-on-an-astral-character-is-a-representation-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "😀b")
                val query = "peek_code"("C")
                assertSolutionEquals(listOf(query.halt(error("peek_code"))), solver.solve(query).toList())
            }
        }

        test("peek-code-leaves-the-astral-character-in-the-stream") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "😀b")
                val query = "catch"("peek_code"("_"), "_", true) and "get_char"("C")
                assertSolutionEquals(listOf(query.yes("C" to "😀")), solver.solve(query).toList())
            }
        }
    })
