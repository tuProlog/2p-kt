package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

/**
 * Regression coverage for parsing native Windows absolute paths (drive letter, `\`-separated)
 * embedded in a `file:` URL string on JS, now via the native (WHATWG) `URL`. `.path` is the URL's
 * path component - normalized to forward slashes, with a leading slash before a drive letter, per
 * spec - which is pure string parsing (no OS involved) and so portable across every CI platform.
 * The actual native-path resolution ([toLocalPath], backed by Node's `url.fileURLToPath`) is
 * inherently host-OS-dependent (there's no `D:` drive to resolve to on Linux/macOS), so it's
 * exercised for real - against the real, current host filesystem - by [TestOpenLocalFile] and the
 * `consult`/`include`/`load` tests instead; that's what Windows CI actually validates.
 */
class WindowsPathParsingSpec :
    FunSpec({
        test("plain-backslash-path-no-forward-slash-at-all") {
            // The common case for a genuine Windows path: no `/` anywhere for a naive parser to anchor on.
            val url = Url.of("""file://C:\Users\me\theory.pl""")
            assertEquals("file", url.protocol)
            assertEquals("", url.host)
            assertEquals("/C:/Users/me/theory.pl", url.path)
        }

        test("two-slash-convention-with-mixed-separators") {
            // Mirrors what jsTest's ResourceUtils.kt builds: process.cwd() (native separators) glued to
            // a forward-slash-only literal suffix.
            val url = Url.of("""file://D:\a\2p-kt\io-lib/kotlin/it/unibo/tuprolog/solve/libs/io/Parents.pl""")
            assertEquals("file", url.protocol)
            assertEquals("/D:/a/2p-kt/io-lib/kotlin/it/unibo/tuprolog/solve/libs/io/Parents.pl", url.path)
        }

        test("three-slash-uri-convention-also-works") {
            val url = Url.of("""file:///D:\a\2p-kt\io-lib\Parents.pl""")
            assertEquals("file", url.protocol)
            assertEquals("/D:/a/2p-kt/io-lib/Parents.pl", url.path)
        }

        test("forward-slash-only-windows-path-still-works") {
            val url = Url.of("file:///D:/a/2p-kt/io-lib/Parents.pl")
            assertEquals("file", url.protocol)
            assertEquals("/D:/a/2p-kt/io-lib/Parents.pl", url.path)
        }

        test("raw-windows-path-without-any-scheme-falls-back-to-file-url") {
            // Exactly what `consult('C:\Users\me\theory.pl')` passes as a raw atom. A native URL parses
            // this "successfully" as scheme "c" (single-letter schemes are otherwise valid URI syntax),
            // so it must be explicitly rejected to force Url.of's file:// fallback.
            val url = Url.of("""C:\Users\me\theory.pl""")
            assertEquals("file", url.protocol)
            assertEquals("/C:/Users/me/theory.pl", url.path)
        }

        test("round-trip-through-to-string-matches-what-consult-does") {
            // This is precisely the failure mode this test suite is guarding against: consult/1,
            // include and load all pass `url.toString()` back through `Url.of`. Compares the *resolved
            // local path* (not the raw `.path` string) since that's the property that actually matters,
            // and doing so is still portable: both sides resolve against the same (current) host.
            val original = Url.file("""D:\a\2p-kt\io-lib\kotlin\Parents.pl""")
            val roundTripped = Url.of(original.toString())
            assertEquals(original.protocol, roundTripped.protocol)
            assertEquals(original.toLocalPath().toString(), roundTripped.toLocalPath().toString())
        }

        test("plain-unix-path-is-unaffected") {
            val url = Url.of("file:///home/user/theory.pl")
            assertEquals("file", url.protocol)
            assertEquals("/home/user/theory.pl", url.path)
        }

        test("unix-shaped-absolute-path-resolves-locally-on-windows-too") {
            // On Windows, Node's fileURLToPath requires a UNC host or a genuine drive-letter prefix,
            // and throws (a TypeError) for a plain Unix-shaped absolute path like this one - even
            // though real fs calls happily resolve it relative to the current drive. toLocalPath must
            // fall back gracefully instead of propagating that crash (this is exactly the shape of path
            // the "missing theory"/"missing include" test fixtures use, on every platform).
            val url = Url.of("/path/to/missing/resource.pl")
            assertEquals("file", url.protocol)
            // Must not throw.
            url.toLocalPath()
        }

        test("http-url-with-bare-root-path-is-unaffected") {
            // Regression check: the file:-URL/drive-letter handling above must not affect http parsing.
            // Non-default port: standard URL parsers normalize away a port matching the scheme default.
            val url = Url.of("http://www.example.com:1234/")
            assertEquals("http", url.protocol)
            assertEquals("www.example.com", url.host)
            assertEquals(1234, url.port)
            assertEquals("/", url.path)
        }
    })
