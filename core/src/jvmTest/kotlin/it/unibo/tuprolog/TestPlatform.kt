package it.unibo.tuprolog

import kotlin.test.Test
import kotlin.test.assertEquals

actual class TestPlatform {
    @Test
    actual fun testCurrentPlatform() {
        assertEquals(Platform.JVM, Info.PLATFORM)
    }
}
