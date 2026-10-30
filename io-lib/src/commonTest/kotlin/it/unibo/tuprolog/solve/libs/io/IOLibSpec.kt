package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.assertEquals

class IOLibSpec :
    FunSpec({
        test("self-equality") {
            assertEquals(IOLib, IOLib)
        }

        test("item-equality") {
            assertEquals(Runtime.of(IOLib), Runtime.of(IOLib))
        }

        test("every-item-is-documented") {
            assertFullyDocumented(IOLib)
        }

        test("documentation-examples-hold") {
            val skip =
                setOf(
                    // read standard input, which would block (or is unsupported, on JS)
                    "at_end_of_stream/0",
                    "read/1",
                    // need an existing file to read, or write to the filesystem
                    "consult/1",
                    "open/3",
                    "open/4",
                )
            assertExamplesHold(IOLib, skip) {
                Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))
            }
        }
    })
