package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsClassNameSpec :
    FunSpec({
        test("resolves-from-global-this-when-require-is-unavailable") {
            val module: dynamic = js("({})")
            module.factory = "factory"
            globalThis["2p-test-solve"] = module

            assertEquals("factory", JsClassName("2p-test-solve", "factory").resolve())
        }

        test("resolves-missing-module-to-null") {
            assertNull(JsClassName("2p-missing-test-solve", "factory").resolve())
        }
    })

private val globalThis: dynamic
    get() = js("globalThis")
