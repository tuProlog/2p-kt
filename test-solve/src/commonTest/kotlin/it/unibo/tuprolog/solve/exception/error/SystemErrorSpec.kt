package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils

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
            underTestError.type shouldBe Atom.of(correctTypeFunctor)
        }

        test("system-error-to-throw-struct-correct") {
            LogicErrorUtils.assertErrorStructCorrect(underTestError)
        }

        test("system-error-type-struct-functor") {
            SystemError.typeFunctor shouldBe correctTypeFunctor
        }
    })
