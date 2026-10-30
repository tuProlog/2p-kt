package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.theory.parsing.ClausesParser
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class UrlSpec :
    FunSpec({
        test("creation1") {
            // A non-default port: standard URL parsers (JS's WHATWG URL included) normalize away a
            // port matching the scheme's default (e.g. 80 for http), so this wouldn't round-trip.
            val urlString = "http://www.example.com:1234/"
            val url = Url.of(urlString)
            assertEquals("http", url.protocol)
            assertEquals("www.example.com", url.host)
            assertEquals(1234, url.port)
            assertEquals("/", url.path)
            assertEquals(urlString, url.toString())
        }

        test("creation2") {
            val urlString = "https://www.example.com"
            val url = Url.of(urlString)
            assertEquals("https", url.protocol)
            assertEquals("www.example.com", url.host)
            assertNull(url.port)
            // For a bare host with no explicit path, java.net.URL (JVM) keeps "" while a WHATWG URL
            // (JS) normalizes it to "/" - both are valid representations of "no path", so accept either.
            assertTrue(url.path.isEmpty() || url.path == "/")
            assertEquals(urlString, url.toString())
        }

        test("creation3") {
            val urlString = "https://www.example.com:8080/path/to/resource?key1=value1&key2=value2#sectionN"
            val url = Url.of(urlString)
            assertEquals("https", url.protocol)
            assertEquals("www.example.com", url.host)
            assertEquals(8080, url.port)
            assertEquals("/path/to/resource", url.path)
            assertEquals("key1=value1&key2=value2", url.query)
            assertEquals(urlString, url.toString())
        }

        test("wrong-prolog-script-retrieval") {
            val url = Url.of(ExampleUrls.WRONG_PARENTS)
            val text = url.readAsText()

            assertSameLines(
                ExampleFiles.WRONG_PARENTS,
                text,
            )
        }

        test("prolog-script-retrieval") {
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

        test("text-retrieval") {
            val url = Url.of(ExampleUrls.HELLO)
            val text = url.readAsText()

            assertTrue {
                text.contains("hello")
            }
        }

        test("bin-retrieval") {
            val url = Url.of(ExampleUrls.RANDOM_BIN)
            val bytes = url.readAsByteArray()
            assertTrue(bytes.isNotEmpty())
            assertEquals(1024, bytes.size)
        }

        test("text-retrieval-failure") {
            val url = Url.of("https://www.invented.nodomain")
            try {
                url.readAsText()
                fail()
            } catch (e: IOException) {
                // success
            }
        }

        test("bin-retrieval-failure") {
            val url = Url.of("https://www.invented.nodomain")
            try {
                url.readAsByteArray()
                fail()
            } catch (e: IOException) {
                // success
            }
        }

        test("local-file-retrieval-as-text") {
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

        test("local-file-retrieval-as-bytes") {
            val url = findResource("random.bin")
            val bytes = url.readAsByteArray()
            assertTrue(bytes.isNotEmpty())
            assertEquals(1024, bytes.size)
        }

        test("url-parsing") {
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
    })
