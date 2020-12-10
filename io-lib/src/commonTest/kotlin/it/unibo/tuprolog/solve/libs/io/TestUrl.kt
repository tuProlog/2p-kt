package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

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
    fun testPrologScriptRetrieval() {
        val url = Url.of("https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/-/snippets/2048665/raw/master/parents.pl")
        val text = url.readAsText()

        assertEquals(
            """
            |male(james1).
            |male(charles1).
            |male(charles2).
            |male(james2).
            |male(george1).
            |
            |female(catherine).
            |female(elizabeth).
            |female(sophia).
            |
            |parent(charles1, james1).
            |parent(elizabeth, james1).
            |parent(charles2, charles1).
            |parent(catherine, charles1).
            |parent(james2, charles1).
            |parent(sophia, elizabeth).
            |parent(george1, sophia).
            |
            |mother(X, Y) :- female(X), parent(X, Y).
            |father(X, Y) :- male(X), parent(X, Y).
            """.trimMargin(),
            text.trim()
        )
    }

    @Test
    fun testTextRetrieval() {
        val url = Url.of("https://www.example.com")
        val text = url.readAsText()

        assertTrue {
            text.contains("<html>")
        }
    }

    @Test
    fun testBinRetrieval() {
        val url = Url.of("https://www.example.com")
        val bytes = url.readAsByteArray()
        assertTrue {
            bytes.isNotEmpty()
        }
    }

    @Test
    fun testTextRetrievalFailure() {
        val url = Url.of("https://www.invented.nodomain")
        try {
            url.readAsText()
            fail()
        } catch (e: IOException) {
            // success
        }
    }

    @Test
    fun testBinRetrievalFailure() {
        val url = Url.of("https://www.invented.nodomain")
        try {
            url.readAsByteArray()
            fail()
        } catch (e: IOException) {
            // success
        }
    }
}
