package it.unibo.tuprolog

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OsTest {
    @Test
    fun testDetectWindows() {
        assertEquals(Os.WINDOWS, Os.detect("Windows 11"))
        assertEquals(Os.WINDOWS, Os.detect("win32"))
    }

    @Test
    fun testDetectMac() {
        assertEquals(Os.MAC, Os.detect("Mac OS X"))
        assertEquals(Os.MAC, Os.detect("macOS"))
    }

    @Test
    fun testDetectLinux() {
        assertEquals(Os.LINUX, Os.detect("Linux 6.1.0"))
    }

    @Test
    fun testDetectAndroid() {
        assertEquals(Os.ANDROID, Os.detect("Android 14"))
        assertEquals(Os.ANDROID, Os.detect("Dalvik 2.1.0"))
    }

    @Test
    fun testDetectUnknown() {
        assertNull(Os.detect("FreeBSD"))
        assertNull(Os.detect("Solaris"))
    }

    @Test
    fun testIsPosix() {
        assertFalse(Os.WINDOWS.isPosix)
        assertTrue(Os.MAC.isPosix)
        assertTrue(Os.LINUX.isPosix)
        assertTrue(Os.ANDROID.isPosix)
        assertTrue(Os.UNKNOWN_POSIX.isPosix)
    }
}
