package it.unibo.tuprolog.solve

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestJsClassName {
    @Test
    fun resolvesFromGlobalThisWhenRequireIsUnavailable() {
        val module: dynamic = js("({})")
        module.factory = "factory"
        globalThis["2p-test-solve"] = module

        assertEquals("factory", JsClassName("2p-test-solve", "factory").resolve())
    }

    @Test
    fun resolvesMissingModuleToNull() {
        assertNull(JsClassName("2p-missing-test-solve", "factory").resolve())
    }

    private val globalThis: dynamic
        get() = js("globalThis")
}
