package it.unibo.tuprolog.solve.libs.io

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regression coverage for parsing native Windows absolute paths (drive letter, `\`-separated)
 * embedded in a `file:` URL string on JS. This is JS-specific because [JsUrl] parses such strings
 * with a hand-rolled regex (`Url.Companion.parse`), unlike the JVM actual, which delegates to
 * `java.net.URI`. Runs on every CI platform (these are pure string-parsing assertions, no real
 * file access), which is exactly why the underlying bug went unnoticed until Windows CI actually
 * exercised `consult`/`include`/`load` against a real Windows path.
 */
class TestWindowsPathParsing {
    @Test
    fun testPlainBackslashPathNoForwardSlashAtAll() {
        // The common case for a genuine Windows path: no `/` anywhere for `hostGroup` to stop at.
        val url = Url.of("""file://C:\Users\me\theory.pl""")
        assertEquals("file", url.protocol)
        assertEquals("", url.host)
        assertEquals("""C:\Users\me\theory.pl""", url.path)
    }

    @Test
    fun testTwoSlashConventionWithMixedSeparators() {
        // Mirrors what jsTest's ResourceUtils.kt builds: process.cwd() (native separators) glued to
        // a forward-slash-only literal suffix.
        val url = Url.of("""file://D:\a\2p-kt\io-lib/kotlin/it/unibo/tuprolog/solve/libs/io/Parents.pl""")
        assertEquals("file", url.protocol)
        assertEquals("""D:\a\2p-kt\io-lib/kotlin/it/unibo/tuprolog/solve/libs/io/Parents.pl""", url.path)
    }

    @Test
    fun testThreeSlashUriConventionStripsTheExtraSlashBeforeTheDriveLetter() {
        val url = Url.of("""file:///D:\a\2p-kt\io-lib\Parents.pl""")
        assertEquals("file", url.protocol)
        assertEquals("""D:\a\2p-kt\io-lib\Parents.pl""", url.path)
    }

    @Test
    fun testForwardSlashOnlyWindowsPathStillWorks() {
        val url = Url.of("file:///D:/a/2p-kt/io-lib/Parents.pl")
        assertEquals("file", url.protocol)
        assertEquals("D:/a/2p-kt/io-lib/Parents.pl", url.path)
    }

    @Test
    fun testRawWindowsPathWithoutAnySchemeFallsBackToFileUrl() {
        // Exactly what `consult('C:\Users\me\theory.pl')` passes as a raw atom.
        val url = Url.of("""C:\Users\me\theory.pl""")
        assertEquals("file", url.protocol)
        assertEquals("""C:\Users\me\theory.pl""", url.path)
    }

    @Test
    fun testRoundTripThroughToStringMatchesWhatConsultDoes() {
        // This is precisely the failure mode this test suite is guarding against: consult/1,
        // include and load all pass `url.toString()` back through `Url.of`.
        val original = Url.file("""D:\a\2p-kt\io-lib\kotlin\Parents.pl""")
        val roundTripped = Url.of(original.toString())
        assertEquals(original.protocol, roundTripped.protocol)
        assertEquals(original.path, roundTripped.path)
    }

    @Test
    fun testPlainUnixPathIsUnaffected() {
        val url = Url.of("file:///home/user/theory.pl")
        assertEquals("file", url.protocol)
        assertEquals("/home/user/theory.pl", url.path)
    }

    @Test
    fun testHttpUrlWithBareRootPathIsUnaffected() {
        // Regression check: the drive-letter slash-stripping must not eat an unrelated bare `/`.
        val url = Url.of("http://www.example.com:80/")
        assertEquals("http", url.protocol)
        assertEquals("www.example.com", url.host)
        assertEquals(80, url.port)
        assertEquals("/", url.path)
    }
}
