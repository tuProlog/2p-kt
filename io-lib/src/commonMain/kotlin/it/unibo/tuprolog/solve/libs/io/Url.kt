package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A parsed source/sink locator, as accepted by the ISO `SourceSink` argument of `open/3,4` and `consult/1`
 * (see [it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsUrl]), and the type this
 * module's stream-opening functions ([openInputChannel], [openOutputChannel]) operate on.
 *
 * A [Url] is platform-specific under the hood (JVM instances wrap `java.net.URL`, JS instances are hand-parsed),
 * but this interface is what every predicate implementation is actually written against; build one via [of] (which
 * also accepts bare file paths, not just `file://`-prefixed ones), [file], [remote], [http], or [https].
 *
 * @see it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
 */
interface Url {
    /** The scheme this [Url] uses, e.g. `"file"`, `"http"`, `"https"`. */
    @JsName("protocol")
    val protocol: String

    /** The host component of this [Url], or the empty string for [isFile] URLs. */
    @JsName("host")
    val host: String

    /** The path component of this [Url], e.g. `/path/to/resource`. */
    @JsName("path")
    val path: String

    /** The port component of this [Url], or `null` if unspecified. */
    @JsName("port")
    val port: Int?

    /** The query component of this [Url] (everything between `?` and an optional `#anchor`), or `null` if absent. */
    @JsName("query")
    val query: String?

    /**
     * Eagerly fetches the resource this [Url] points to, decoded as UTF-8 text.
     * @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the resource cannot be read (e.g. missing
     * file, unreachable host).
     */
    @JsName("readAsText")
    fun readAsText(): String

    /**
     * Eagerly fetches the resource this [Url] points to, as raw bytes.
     * @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the resource cannot be read (e.g. missing
     * file, unreachable host).
     */
    @JsName("readAsByteArray")
    fun readAsByteArray(): ByteArray

    /** Builds the [Url] obtained by appending [child] as a further path segment of this one. */
    @JsName("resolve")
    fun resolve(child: String): Url = remote(protocol, host, port, "$path/$child", query)

    /** Operator alias for [resolve], so that `url / "child"` reads like a path-append. */
    @JsName("div")
    operator fun div(child: String): Url = resolve(child)

    /** Whether this [Url]'s [protocol] is `"file"`. */
    @JsName("isFile")
    val isFile: Boolean
        get() = protocol == "file"

    /** Whether this [Url]'s [protocol] starts with `"http"` (i.e. is `"http"` or `"https"`). */
    @JsName("isHttp")
    val isHttp: Boolean
        get() = protocol.startsWith("http")

    companion object {
        private fun <T : Any> T?.str(transformation: (T) -> String = { it.toString() }): String =
            if (this == null) "" else transformation(this)

        internal fun Int.ensureValidPort(): Int =
            if (this < 0) {
                throw IllegalArgumentException(
                    "Invalid port: $this",
                )
            } else {
                this
            }

        /** Builds a `file://`-scheme [Url] pointing at [path] (platform-specific: filesystem path on the JVM). */
        @JvmStatic
        @JsName("file")
        fun file(path: String): Url = fileUrl(path)

        /** Builds a [Url] with the given [protocol] and, optionally, [host], [port], [path] and [query]. */
        @JvmStatic
        @JsName("remote")
        fun remote(
            protocol: String,
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): Url = remoteUrl(protocol, host, port, path, query)

        /** Shorthand for [remote] with `protocol = "http"`. */
        @JvmStatic
        @JsName("http")
        fun http(
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): Url = remote("http", host, port, path, query)

        /** Shorthand for [remote] with `protocol = "https"`. */
        @JvmStatic
        @JsName("https")
        fun https(
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): Url = remote("https", host, port, path, query)

        /**
         * Parses [string] into a [Url], e.g. `Url.of("https://example.com/page")`.
         *
         * As a fallback for bare filesystem paths (which are not well-formed URLs on their own), if [string] fails
         * to parse as-is, parsing is retried after prefixing it with `"file://"`; this is what lets
         * [it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsUrl] accept both
         * `open('file:///tmp/x.pl', ...)` and `open('/tmp/x.pl', ...)` (or a relative path) as valid source/sinks.
         *
         * @throws InvalidUrlException if [string] is not a well-formed URL even after the `file://` fallback.
         */
        @JvmStatic
        @JsName("of")
        fun of(string: String): Url =
            try {
                parseUrl(string)
            } catch (e: InvalidUrlException) {
                try {
                    parseUrl("file://$string")
                } catch (_: InvalidUrlException) {
                    throw e
                }
            }

        internal fun toString(
            protocol: String,
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): String = "$protocol://$host${port?.ensureValidPort().str { ":$it" }}$path${query.str { "?$it" }}"
    }
}
