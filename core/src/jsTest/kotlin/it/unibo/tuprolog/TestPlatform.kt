package it.unibo.tuprolog

import kotlin.test.Test
import kotlin.test.assertEquals

actual class TestPlatform {
    @Test
    actual fun testCurrentPlatform() {
        // assumes testing is performed on node
        assertEquals(Platform.NODE, Info.PLATFORM)
    }
}
