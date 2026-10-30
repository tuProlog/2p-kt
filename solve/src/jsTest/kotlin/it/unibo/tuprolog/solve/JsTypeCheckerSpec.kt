package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import kotlin.test.assertTrue

class JsTypeCheckerSpec :
    FunSpec({
        test("instantiation-error-klass-is-substitution-fail") {
            assertTrue(InstantiationError::class !is Substitution.Fail)
        }

        test("logic-error-klass-is-substitution-fail") {
            assertTrue(LogicError::class !is Substitution.Fail)
        }

        test("resolution-exception-klass-is-substitution-fail") {
            assertTrue(ResolutionException::class !is Substitution.Fail)
        }

        test("tu-prolog-exception-klass-is-substitution-fail") {
            assertTrue(TuPrologException::class !is Substitution.Fail)
        }

        test("runtime-exception-klass-is-substitution-fail") {
            assertTrue(RuntimeException::class !is Substitution.Fail)
        }

        test("exception-klass-is-substitution-fail") {
            assertTrue(Exception::class !is Substitution.Fail)
        }

        test("any-klass-is-substitution-fail") {
            assertTrue(Any::class !is Substitution.Fail)
        }
    })
