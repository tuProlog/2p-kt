package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.test.assertEquals

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
            assertEquals("error", ErrorUtils.ERROR_FUNCTOR)
        }

        test("error-struct-of-with-args-creates-correct-struct") {
            assertEquals(
                Struct.of(ErrorUtils.ERROR_FUNCTOR, errorDescription, errorExtraData),
                ErrorUtils.errorStructOf(errorDescription, errorExtraData),
            )
        }

        test("error-struct-of-with-only-first-arg-fills-extra-data-with-true") {
            assertEquals(
                Struct.of(ErrorUtils.ERROR_FUNCTOR, errorDescription, defaultCustomErrorData),
                ErrorUtils.errorStructOf(errorDescription),
            )
        }
    })
