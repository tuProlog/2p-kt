package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.ANCHOR
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.HOST
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PATH
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PORT
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.PROTOCOL
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.QUERY
import it.unibo.tuprolog.solve.libs.io.Url.Companion.UrlField.UNIT
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

    @JsName("readAsText")
    fun readAsText(): String

    @JsName("readAsByteArray")
    fun readAsByteArray(): ByteArray

    @JsName("resolve")
    fun resolve(child: String): Url

    @JsName("div")
    operator fun div(child: String): Url = resolve(child)

    companion object {

        internal enum class UrlField { PROTOCOL, UNIT, HOST, PORT, PATH, QUERY, ANCHOR }

        @JvmStatic
        val URL_REGEX = Regex("""(?i)\b(?<$PROTOCOL>[\w]+):\/\/(?:(?<$UNIT>[a-z]\:)|(?<$HOST>[^\s]+[.][a-z]{2,4})(?:\:(?<$PORT>\d+))?)(?<$PATH>(?:\/[^\s?\#\/]+)+)?(?:\/)?(\?(?<$QUERY>[^\s\/?#]+)?)?(?:\#(?<$ANCHOR>.*))?""")

        internal fun parse(string: String): Map<UrlField, String>? {
            return URL_REGEX.matchEntire(string)
                ?.groups
                ?.let { it as? MatchNamedGroupCollection }
                ?.let { group ->
                    UrlField.values()
                        .map { it to (group[it.toString()]?.value ?: "") }
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
    }
}
