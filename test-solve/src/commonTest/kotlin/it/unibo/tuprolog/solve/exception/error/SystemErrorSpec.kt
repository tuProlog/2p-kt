package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import kotlin.test.assertEquals

/**
 * Test class for [SystemError]
 *
 * @author Enrico
 */
class SystemErrorSpec :
    FunSpec({
        val underTestError = SystemError(context = LogicErrorUtils.aContext)

        val correctTypeFunctor = "system_error"

        test("system-error-type-correct") {
            assertEquals(Atom.of(correctTypeFunctor), underTestError.type)
        }

        test("system-error-to-throw-struct-correct") {
            LogicErrorUtils.assertErrorStructCorrect(underTestError)
        }

        test("system-error-type-struct-functor") {
            assertEquals(correctTypeFunctor, SystemError.typeFunctor)
        }
    })
