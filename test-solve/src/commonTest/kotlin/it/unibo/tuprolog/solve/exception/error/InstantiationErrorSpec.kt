package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import kotlin.test.assertEquals

/**
 * Test class for [InstantiationError]
 *
 * @author Enrico
 */
class InstantiationErrorSpec :
    FunSpec({
        val underTestError = InstantiationError(context = LogicErrorUtils.aContext)

        val correctTypeFunctor = "instantiation_error"

        test("instantiation-error-type-correct") {
            assertEquals(Atom.of(correctTypeFunctor), underTestError.type)
        }

        test("instantiation-error-to-throw-struct-correct") {
            LogicErrorUtils.assertErrorStructCorrect(underTestError)
        }

        test("instantiation-error-type-struct-functor") {
            assertEquals(correctTypeFunctor, InstantiationError.typeFunctor)
        }
    })
