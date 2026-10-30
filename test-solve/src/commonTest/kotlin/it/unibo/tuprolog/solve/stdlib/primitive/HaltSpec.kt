package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.createSolveRequest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Halt]
 *
 * @author Enrico
 */
class HaltSpec :
    FunSpec({
        val haltSolveRequest = createSolveRequest(Atom.of("halt"))

        test("halt-primitive-throws-halt-exception") {
            assertFailsWith<HaltException> { Halt.implementation.solve(haltSolveRequest) }
        }

        test("halt-primitive-exception-contains-correct-context") {
            assertOverFailure<ResolutionException>({ Halt.implementation.solve(haltSolveRequest) }) {
                assertEquals(haltSolveRequest.context, it.context)
            }
        }
    })
