package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldNotBeInstanceOf
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError

class JsTypeCheckerSpec :
    FunSpec({
        test("instantiation-error-klass-is-substitution-fail") {
            InstantiationError::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }

        test("logic-error-klass-is-substitution-fail") {
            LogicError::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }

        test("resolution-exception-klass-is-substitution-fail") {
            ResolutionException::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }

        test("tu-prolog-exception-klass-is-substitution-fail") {
            TuPrologException::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }

        test("runtime-exception-klass-is-substitution-fail") {
            RuntimeException::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }

        test("exception-klass-is-substitution-fail") {
            Exception::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }

        test("any-klass-is-substitution-fail") {
            Any::class.shouldNotBeInstanceOf<Substitution.Fail>()
        }
    })
