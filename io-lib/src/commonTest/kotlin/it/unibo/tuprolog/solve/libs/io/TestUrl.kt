package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.theory.parsing.ClausesParser
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
    fun testWrongPrologScriptRetrieval() {
        val url = Url.of(ExampleUrls.WRONG_PARENTS)
        val text = url.readAsText()

        assertSameLines(
            ExampleFiles.WRONG_PARENTS,
            text,
        )
    }

    @Test
    fun testPrologScriptRetrieval() {
        val url = Url.of(ExampleUrls.PARENTS)
        println(url)
        val text = url.readAsText()

        assertSameLines(
            ExampleFiles.PARENTS,
            text,
        )

        val parsed = ClausesParser.withDefaultOperators().parseTheory(text)

        assertTrue {
            ExampleTheories.PARENTS.equals(parsed, useVarCompleteName = false)
        }
    }

    @Test
    fun testTextRetrieval() {
        val url = Url.of("http://localhost:8080/hello")
        val text = url.readAsText()

        assertTrue {
            text.contains("hello")
        }
    }

    @Test
    fun testBinRetrieval() {
        val url = Url.of("http://localhost:8080/random.bin")
        val bytes = url.readAsByteArray()
        assertTrue(bytes.isNotEmpty())
        assertEquals(1024, bytes.size)
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

    @Test
    fun testLocalFileRetrievalAsText() {
        var url = findResource("Parents.pl")
        assertSameLines(
            ExampleFiles.PARENTS,
            url.readAsText(),
        )
        url = findResource("WrongParents.pl")
        assertSameLines(
            ExampleFiles.WRONG_PARENTS,
            url.readAsText(),
        )
    }

    @Test
    fun testLocalFileRetrievalAsBytes() {
        val url = findResource("random.bin")
        val bytes = url.readAsByteArray()
        assertTrue(bytes.isNotEmpty())
        assertEquals(1024, bytes.size)
    }

    @Test
    fun testUrlParsing() {
        var url = findResource("Parents.pl")
        assertEquals("file", url.protocol)
        assertTrue { url.host.isBlank() }
        assertNull(url.port)
        assertNull(url.query)
        assertTrue { url.isFile }
        assertTrue { url.path.endsWith("/Parents.pl") }
        url = Url.of(url.toString())
        assertEquals("file", url.protocol)
        assertTrue { url.host.isBlank() }
        assertNull(url.port)
        assertNull(url.query)
        assertTrue { url.isFile }
        assertTrue { url.path.endsWith("/Parents.pl") }
    }
}
