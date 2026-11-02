package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct

/**
 * Test class for [ErrorUtils]
 *
 * @author Enrico
 */
class ErrorUtilsSpec :
    FunSpec({
        val errorDescription = Atom.of("myErrorDesc")

        val errorExtraData = Atom.of("myExtra")

        val defaultCustomErrorData = Atom.of("")

        test("standard-error-wrapper-functor-correct") {
            ErrorUtils.ERROR_FUNCTOR shouldBe "error"
        }

        test("error-struct-of-with-args-creates-correct-struct") {
            ErrorUtils.errorStructOf(errorDescription, errorExtraData) shouldBe
                Struct.of(ErrorUtils.ERROR_FUNCTOR, errorDescription, errorExtraData)
        }

        test("error-struct-of-with-only-first-arg-fills-extra-data-with-true") {
            ErrorUtils.errorStructOf(errorDescription) shouldBe
                Struct.of(ErrorUtils.ERROR_FUNCTOR, errorDescription, defaultCustomErrorData)
        }
    })
