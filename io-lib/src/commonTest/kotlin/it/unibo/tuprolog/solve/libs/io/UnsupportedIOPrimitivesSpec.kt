package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory

/**
 * Byte input/output (`get_byte`, `peek_byte`, `put_byte`), `close/2` and the character-conversion
 * predicates (`char_conversion/2`, `current_char_conversion/2`) are registered by [IOLib] but not
 * actually implemented: they unconditionally raise a `system_error` reporting that the operation is
 * not supported, regardless of their arguments. This documents that consistent, deliberate behavior
 * so a future partial implementation does not silently start returning wrong results instead of a
 * clear error - and so it's obvious at a glance that these are gaps, not just uncovered primitives.
 */
class UnsupportedIOPrimitivesSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        fun assertNotSupported(
            functor: String,
            arity: Int,
            query: Struct,
        ) {
            val solver = PrologSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        SystemError.forUncaughtException(
                            ctx,
                            IllegalStateException("Operation $functor/$arity is not supported"),
                        ),
                    ),
                ),
                solutions,
            )
        }

        test("get-byte1-is-not-supported") {
            logicProgramming { assertNotSupported("get_byte", 1, "get_byte"("B")) }
        }

        test("get-byte2-is-not-supported") {
            logicProgramming { assertNotSupported("get_byte", 2, "get_byte"("mickey", "B")) }
        }

        test("peek-byte1-is-not-supported") {
            logicProgramming { assertNotSupported("peek_byte", 1, "peek_byte"("B")) }
        }

        test("peek-byte2-is-not-supported") {
            logicProgramming { assertNotSupported("peek_byte", 2, "peek_byte"("mickey", "B")) }
        }

        test("put-byte1-is-not-supported") {
            logicProgramming { assertNotSupported("put_byte", 1, "put_byte"(65)) }
        }

        test("put-byte2-is-not-supported") {
            logicProgramming { assertNotSupported("put_byte", 2, "put_byte"("mickey", 65)) }
        }

        test("close2-is-not-supported") {
            logicProgramming {
                assertNotSupported(
                    "close",
                    2,
                    "close"(
                        "mickey",
                        it.unibo.tuprolog.core.List
                            .empty(),
                    ),
                )
            }
        }

        test("char-conversion2-is-not-supported") {
            logicProgramming { assertNotSupported("char_conversion", 2, "char_conversion"("a", "b")) }
        }

        test("current-char-conversion2-is-not-supported") {
            logicProgramming { assertNotSupported("current_char_conversion", 2, "current_char_conversion"("A", "B")) }
        }
    })
