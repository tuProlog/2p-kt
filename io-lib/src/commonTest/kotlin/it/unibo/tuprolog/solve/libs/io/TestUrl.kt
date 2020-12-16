package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.theory.parsing.ClausesParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class TestUrl {

    private val testScriptsBaseUrl = "https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/-/snippets/2048665/raw/master"

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
        val url = Url.of("$testScriptsBaseUrl/parents-wrong.pl")
        val text = url.readAsText()

        assertEquals(
            ExampleFiles.WRONG_PARENTS,
            text.trim()
        )
    }

    @Test
    fun testPrologScriptRetrieval() {
        val url = Url.of("$testScriptsBaseUrl/parents.pl")
        val text = url.readAsText()

        assertEquals(
            ExampleFiles.PARENTS,
            text.trim()
        )

        val parsed = ClausesParser.withDefaultOperators.parseTheory(text)

        assertTrue {
            ExampleTheories.PARENTS.equals(parsed, useVarCompleteName = false)
        }
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

    @Test
    fun testLocalFileRetrievalAsText() {
        var url = findResource("Parents.pl")
        assertEquals(
            ExampleFiles.PARENTS,
            url.readAsText().trim()
        )
        url = findResource("WrongParents.pl")
        assertEquals(
            ExampleFiles.WRONG_PARENTS,
            url.readAsText().trim()
        )
    }

    @Test
    fun testLocalFileRetrievalAsBytes() {
        var url = findResource("Parents.pl")
        assertEquals(
            url.readAsText().trim(),
            url.readAsByteArray().decodeToString().trim()
        )
        url = findResource("WrongParents.pl")
        assertEquals(
            url.readAsText().trim(),
            url.readAsByteArray().decodeToString().trim()
        )
    }
}
