package it.unibo.tuprolog.solve

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestJsClassName {
    @Test
    fun resolvesModulesRegisteredOnGlobalThisWhenRequireFails() {
        js("globalThis['2p-fake-module'] = { a: { b: 42 } }")
        assertEquals(42, JsClassName.parse("./2p-fake-module:a.b").resolve())
    }

    @Test
    fun resolvesToNullForUnknownModules() {
        assertNull(JsClassName.parse("./2p-missing-module:a.b").resolve())
    }
}
