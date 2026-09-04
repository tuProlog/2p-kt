package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
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
    fun resolve(child: String): Url = remote(protocol, host, port, "$path/$child", query)

    @JsName("div")
    operator fun div(child: String): Url = resolve(child)

    @JsName("isFile")
    val isFile: Boolean
        get() = protocol == "file"

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

        @JvmStatic
        @JsName("file")
        fun file(path: String): Url = fileUrl(path)

        @JvmStatic
        @JsName("remote")
        fun remote(
            protocol: String,
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): Url = remoteUrl(protocol, host, port, path, query)

        @JvmStatic
        @JsName("http")
        fun http(
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): Url = remote("http", host, port, path, query)

        @JvmStatic
        @JsName("https")
        fun https(
            host: String = "",
            port: Int? = null,
            path: String = "",
            query: String? = null,
        ): Url = remote("https", host, port, path, query)

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
