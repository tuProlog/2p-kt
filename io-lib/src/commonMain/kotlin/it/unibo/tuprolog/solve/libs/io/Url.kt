package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.ANCHOR
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.HOST
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PATH
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PORT
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PROTOCOL
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.QUERY
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.UNIT
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Url {
    @JsName("protocol")
    val protocol: String

    @JsName("host")
    val host: String

    @JsName("path")
    val path: String

    @JsName("port")
    val port: Int?

    @JsName("query")
    val query: String?

    @JsName("readAsTextAsync")
    fun readAsTextAsync(callback: (String?, IOException?) -> Unit)

    @JsName("readAsByteArrayAsync")
    fun readAsByteArrayAsync(callback: (ByteArray?, IOException?) -> Unit)

    @JsName("readAsText")
    fun readAsText(): String

    @JsName("readAsByteArray")
    fun readAsByteArray(): ByteArray

    @JsName("resolve")
    fun resolve(child: String): Url =
        remote(protocol, host, port, "$path/$child", query)

    @JsName("div")
    operator fun div(child: String): Url = resolve(child)

    @JsName("isFile")
    val isFile: Boolean
        get() = protocol == "file" && host.isBlank() && port == null && query == null

    @JsName("isHttp")
    val isHttp: Boolean
        get() = protocol.startsWith("http") && host.isNotBlank()

    companion object {

        internal fun <T : Any> T?.str(transformation: (T) -> String = { it.toString() }): String =
            if (this == null) "" else transformation(this)

        internal fun Int.ensureValidPort(): Int =
            if (this < 0) throw IllegalArgumentException("Invalid port: $this") else this

        internal enum class UrlField { PROTOCOL, UNIT, HOST, PORT, PATH, QUERY, ANCHOR }

        @JvmStatic
        val URL_REGEX = Regex(
            """(?<$PROTOCOL>[\w]+):\/\/(?:(?<$UNIT>[a-z]\:)|(?<$HOST>[^\s]+[.][a-z]{2,4})(?:\:(?<$PORT>\d+))?)(?<$PATH>(?:\/[^\s?\#\/]+)+)?(?:\/)?(\?(?<$QUERY>[^\s\/?#]+)?)?(?:\#(?<$ANCHOR>.*))?""",
            RegexOption.IGNORE_CASE
        )

        internal fun parse(string: String): Map<UrlField, String?>? {
            val match = URL_REGEX.matchEntire(string)?.groups?.toList()
            return match
                // ?.let { it as? MatchNamedGroupCollection }
                ?.let { groups ->
                    UrlField.values()
                        .map { it to (groups[it.ordinal]?.value) }
                        .toMap()
                }
        }

        @JvmStatic
        @JsName("file")
        fun file(path: String): Url =
            fileUrl(path)

        @JvmStatic
        @JsName("remote")
        fun remote(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null): Url =
            remoteUrl(protocol, host, port, path, query)

        @JvmStatic
        @JsName("http")
        fun http(host: String = "", port: Int? = null, path: String = "", query: String? = null): Url =
            remote("http", host, port, path, query)

        @JvmStatic
        @JsName("https")
        fun https(host: String = "", port: Int? = null, path: String = "", query: String? = null): Url =
            remote("https", host, port, path, query)

        @JvmStatic
        @JsName("of")
        fun of(string: String): Url = parseUrl(string)

        internal fun toString(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null): String =
            "$protocol://$host${port?.ensureValidPort()?.str { ":$it" }}$path${query.str { "?$it" }}"
    }
}
