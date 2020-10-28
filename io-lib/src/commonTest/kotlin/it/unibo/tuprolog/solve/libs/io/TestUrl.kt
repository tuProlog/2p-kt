package it.unibo.tuprolog.solve.libs.io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestUrl {
    @Test
    fun testRemote() {
        val url = Url.of("http://www.example.com:80/")
        assertEquals("http", url.protocol)
        assertEquals("www.example.com", url.host)
        assertEquals(80, url.port)
        assertEquals("/", url.path)
        assertNull(url.query)
        assertTrue {
            url.readAsText().contains("<html>")
        }
    }
}