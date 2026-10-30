package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.assertEquals

class DefaultBuiltinsSpec :
    FunSpec({
        test("self-equality") {
            assertEquals(DefaultBuiltins, DefaultBuiltins)
        }

        test("item-equality") {
            assertEquals(Runtime.of(DefaultBuiltins), Runtime.of(DefaultBuiltins))
        }

        test("every-item-is-documented") {
            assertFullyDocumented(DefaultBuiltins)
        }
    })
