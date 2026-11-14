package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import kotlin.time.Duration.Companion.seconds

/** A primitive failing with an exception that is not a resolution error, as a buggy library's would. */
private val crashing =
    Library.of(
        "crashing",
        primitives = mapOf(Signature("crash", 0) to Primitive { throw IllegalStateException("bug") }),
    )

/** Unexpected errors end resolutions with a halt, rather than leaving their solutions waited for forever. */
class ConcurrentUnexpectedErrorsSpec :
    FunSpec({
        test("unexpected-errors-halt-the-resolution").config(timeout = 30.seconds, blockingTest = true) {
            val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(crashing))
            val solution = solver.solveOnce(Struct.of(",", Atom.of("true"), Atom.of("crash")), SolveOptions.DEFAULT)
            solution.shouldBeInstanceOf<Solution.Halt>()
            solution.exception.shouldBeInstanceOf<SystemError>()
        }
    })
