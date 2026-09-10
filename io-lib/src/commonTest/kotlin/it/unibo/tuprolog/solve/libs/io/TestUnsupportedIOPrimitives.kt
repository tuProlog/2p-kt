package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.Test

/**
 * Byte input/output (`get_byte`, `peek_byte`, `put_byte`), `close/2` and the character-conversion
 * predicates (`char_conversion/2`, `current_char_conversion/2`) are registered by [IOLib] but not
 * actually implemented: they unconditionally raise a `system_error` reporting that the operation is
 * not supported, regardless of their arguments. This documents that consistent, deliberate behavior
 * so a future partial implementation does not silently start returning wrong results instead of a
 * clear error - and so it's obvious at a glance that these are gaps, not just uncovered primitives.
 */
class TestUnsupportedIOPrimitives {
    private val ctx = DummyInstances.executionContext

    private fun assertNotSupported(
        functor: String,
        arity: Int,
        query: Struct,
    ) {
        val solver = ClassicSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))
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

    @Test
    fun testGetByte1IsNotSupported() {
        logicProgramming { assertNotSupported("get_byte", 1, "get_byte"("B")) }
    }

    @Test
    fun testGetByte2IsNotSupported() {
        logicProgramming { assertNotSupported("get_byte", 2, "get_byte"("mickey", "B")) }
    }

    @Test
    fun testPeekByte1IsNotSupported() {
        logicProgramming { assertNotSupported("peek_byte", 1, "peek_byte"("B")) }
    }

    @Test
    fun testPeekByte2IsNotSupported() {
        logicProgramming { assertNotSupported("peek_byte", 2, "peek_byte"("mickey", "B")) }
    }

    @Test
    fun testPutByte1IsNotSupported() {
        logicProgramming { assertNotSupported("put_byte", 1, "put_byte"(65)) }
    }

    @Test
    fun testPutByte2IsNotSupported() {
        logicProgramming { assertNotSupported("put_byte", 2, "put_byte"("mickey", 65)) }
    }

    @Test
    fun testClose2IsNotSupported() {
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

    @Test
    fun testCharConversion2IsNotSupported() {
        logicProgramming { assertNotSupported("char_conversion", 2, "char_conversion"("a", "b")) }
    }

    @Test
    fun testCurrentCharConversion2IsNotSupported() {
        logicProgramming { assertNotSupported("current_char_conversion", 2, "current_char_conversion"("A", "B")) }
    }
}
