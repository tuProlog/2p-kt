package it.unibo.tuprolog.solve.prolog.stdlib

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.assertEquals

class DefaultBuiltinsSpec :
    FunSpec({
        test("default-builtins/self-equality") {
            assertEquals(DefaultBuiltins, DefaultBuiltins)
        }

        test("default-builtins/runtime-equality") {
            assertEquals(Runtime.of(DefaultBuiltins), Runtime.of(DefaultBuiltins))
        }
    })
