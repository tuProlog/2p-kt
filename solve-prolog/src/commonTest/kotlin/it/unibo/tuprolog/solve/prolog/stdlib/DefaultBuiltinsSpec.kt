package it.unibo.tuprolog.solve.prolog.stdlib

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.library.Runtime

class DefaultBuiltinsSpec :
    FunSpec({
        test("default-builtins/self-equality") {
            DefaultBuiltins shouldBe DefaultBuiltins
        }

        test("default-builtins/runtime-equality") {
            Runtime.of(DefaultBuiltins) shouldBe Runtime.of(DefaultBuiltins)
        }
    })
