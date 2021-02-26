package it.unibo.tuprolog

import org.junit.Test
import kotlin.test.assertEquals

actual class TestPlatform {
    @Test
    actual fun testPlatform() {
        assertEquals(Platform.JVM, Info.PLATFORM)
    }
}
