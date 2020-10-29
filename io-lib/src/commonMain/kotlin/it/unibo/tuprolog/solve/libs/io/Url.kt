package it.unibo.tuprolog.solve.libs.io

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

        private fun <T : Any> T?.str(transformation: (T) -> String = { it.toString() }): String =
            if (this == null) "" else transformation(this)

        internal fun Int.ensureValidPort(): Int =
            if (this < 0) throw IllegalArgumentException("Invalid port: $this") else this

        internal enum class UrlField { PROTOCOL, UNIT, HOST, PORT, PATH, QUERY, ANCHOR }

        private fun urlRegex(protocol: String? = null, unit: String? = null, host: String? = null, port: String? = null, path: String? = null, query: String? = null, anchor: String? = null): Regex {
            val protocolGroup =
                """(${protocol.str { "?<$it>" }}[\w]+)"""
            val unitGroup =
                """(\/?${unit.str { "?$it" }}[a-z]\:)"""
            val hostGroup =
                """(${host.str { "?<$it>" }}[^\s]+[.][a-z]{2,})"""
            val portGroup =
                """(${port.str { "?<$it>" }}\d+)"""
            val pathGroup =
                """(${path.str { "?<$it>" }}(?:\/[^\s?\#\/]+)*\/?)"""
            val queryGroup =
                """(${query.str { "?<$it>" }}[^\s\/?#]+)"""
            val anchorGroup =
                """(${anchor.str { "?<$it>" }}.*)"""
            return Regex(
                """$protocolGroup\:\/+(?:$unitGroup|$hostGroup(?:\:$portGroup)?)$pathGroup(?:\?$queryGroup?)?(?:\#$anchorGroup)?""",
                RegexOption.IGNORE_CASE
            )
        }

        @JvmStatic
        val URL_REGEX = urlRegex()

        internal fun parse(string: String): Map<UrlField, String?>? {
            val match = URL_REGEX.matchEntire(string)?.groups?.toList()
            return match
                // ?.let { it as? MatchNamedGroupCollection }
                ?.let { groups ->
                    UrlField.values()
                        .map { it to (groups[it.ordinal + 1]?.value) }
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
