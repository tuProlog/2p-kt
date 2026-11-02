package it.unibo.tuprolog.solve.libs.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.theory.parsing.ClausesParser

class UrlSpec :
    FunSpec({
        test("creation1") {
            // A non-default port: standard URL parsers (JS's WHATWG URL included) normalize away a
            // port matching the scheme's default (e.g. 80 for http), so this wouldn't round-trip.
            val urlString = "http://www.example.com:1234/"
            val url = Url.of(urlString)
            url.protocol shouldBe "http"
            url.host shouldBe "www.example.com"
            url.port shouldBe 1234
            url.path shouldBe "/"
            url.toString() shouldBe urlString
        }

        test("creation2") {
            val urlString = "https://www.example.com"
            val url = Url.of(urlString)
            url.protocol shouldBe "https"
            url.host shouldBe "www.example.com"
            url.port.shouldBeNull()
            // For a bare host with no explicit path, java.net.URL (JVM) keeps "" while a WHATWG URL
            // (JS) normalizes it to "/" - both are valid representations of "no path", so accept either.
            (url.path.isEmpty() || url.path == "/").shouldBeTrue()
            url.toString() shouldBe urlString
        }

        test("creation3") {
            val urlString = "https://www.example.com:8080/path/to/resource?key1=value1&key2=value2#sectionN"
            val url = Url.of(urlString)
            url.protocol shouldBe "https"
            url.host shouldBe "www.example.com"
            url.port shouldBe 8080
            url.path shouldBe "/path/to/resource"
            url.query shouldBe "key1=value1&key2=value2"
            url.toString() shouldBe urlString
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

            ExampleTheories.PARENTS.equals(parsed, useVarCompleteName = false).shouldBeTrue()
        }

        test("text-retrieval") {
            val url = Url.of(ExampleUrls.HELLO)
            val text = url.readAsText()

            text.contains("hello").shouldBeTrue()
        }

        test("bin-retrieval") {
            val url = Url.of(ExampleUrls.RANDOM_BIN)
            val bytes = url.readAsByteArray()
            bytes.isNotEmpty().shouldBeTrue()
            bytes.size shouldBe 1024
        }

        test("text-retrieval-failure") {
            val url = Url.of("https://www.invented.nodomain")
            shouldThrow<IOException> { url.readAsText() }
        }

        test("bin-retrieval-failure") {
            val url = Url.of("https://www.invented.nodomain")
            shouldThrow<IOException> { url.readAsByteArray() }
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
            bytes.isNotEmpty().shouldBeTrue()
            bytes.size shouldBe 1024
        }

        test("url-parsing") {
            var url = findResource("Parents.pl")
            url.protocol shouldBe "file"
            url.host.isBlank().shouldBeTrue()
            url.port.shouldBeNull()
            url.query.shouldBeNull()
            url.isFile.shouldBeTrue()
            url.path.endsWith("/Parents.pl").shouldBeTrue()
            url = Url.of(url.toString())
            url.protocol shouldBe "file"
            url.host.isBlank().shouldBeTrue()
            url.port.shouldBeNull()
            url.query.shouldBeNull()
            url.isFile.shouldBeTrue()
            url.path.endsWith("/Parents.pl").shouldBeTrue()
        }
    })
