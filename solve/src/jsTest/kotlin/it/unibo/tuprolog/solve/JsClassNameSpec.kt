package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class JsClassNameSpec :
    FunSpec({
        test("resolves-from-global-this-when-require-is-unavailable") {
            val module: dynamic = js("({})")
            module.factory = "factory"
            globalThis["2p-test-solve"] = module
            // resolve() is dynamic, and extension functions (Kotest matchers) are not callable on dynamic values
            val resolved: Any? = JsClassName("2p-test-solve", "factory").resolve()
            resolved shouldBe "factory"
        }

        test("resolves-missing-module-to-null") {
            // resolve() is dynamic, and extension functions (Kotest matchers) are not callable on dynamic values
            val resolved: Any? = JsClassName("2p-missing-test-solve", "factory").resolve()
            resolved.shouldBeNull()
        }
    })

private val globalThis: dynamic
    get() = js("globalThis")
