package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.library.Runtime

class DefaultBuiltinsSpec :
    FunSpec({
        test("self-equality") {
            DefaultBuiltins shouldBe DefaultBuiltins
        }

        test("item-equality") {
            Runtime.of(DefaultBuiltins) shouldBe Runtime.of(DefaultBuiltins)
        }

        test("every-item-is-documented") {
            assertFullyDocumented(DefaultBuiltins)
        }
    })
