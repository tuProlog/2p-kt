package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestUrl {
    @Test
    fun testCreation1() {
        val urlString = "http://www.example.com:80/"
        val url = Url.of(urlString)
        assertEquals("http", url.protocol)
        assertEquals("www.example.com", url.host)
        assertEquals(80, url.port)
        assertEquals("/", url.path)
        assertEquals(urlString, url.toString())
    }

    @Test
    fun testCreation2() {
        val urlString = "https://www.example.com"
        val url = Url.of(urlString)
        assertEquals("https", url.protocol)
        assertEquals("www.example.com", url.host)
        assertNull(url.port)
        assertEquals("", url.path)
        assertEquals(urlString, url.toString())
    }

    @Test
    fun testCreation3() {
        val urlString = "https://www.example.com:8080/path/to/resource?key1=value1&key2=value2#sectionN"
        val url = Url.of(urlString)
        assertEquals("https", url.protocol)
        assertEquals("www.example.com", url.host)
        assertEquals(8080, url.port)
        assertEquals("/path/to/resource", url.path)
        assertEquals("key1=value1&key2=value2", url.query)
        assertEquals(urlString, url.toString())
    }

    @Test
    fun testAsyncTextRetrieval() {
        val url = Url.of("https://www.example.com")
        url.readAsTextAsync { text, err ->
            assertNull(err)
            assertTrue {
                text?.contains("<html>") ?: false
            }
        }
    }

    @Test
    fun testAsyncBinRetrieval() {
        val url = Url.of("https://www.example.com")
        url.readAsByteArrayAsync { bytes, err ->
            assertNull(err)
            assertTrue {
                bytes?.size?.let { it > 0 } ?: false
            }
        }
    }

    @Test
    fun testAsyncTextRetrievalFailure() {
        val url = Url.of("https://www.invented.nodomain")
        url.readAsTextAsync { text, err ->
            assertNull(text)
            assertTrue { err is IOException }
        }
    }

    @Test
    fun testAsyncBinRetrievalFailure() {
        val url = Url.of("https://www.invented.nodomain")
        url.readAsByteArrayAsync { bytes, err ->
            assertNull(bytes)
            assertTrue { err is IOException }
        }
    }
}
