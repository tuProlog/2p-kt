@file:JvmName("UrlUtils")

package it.unibo.tuprolog.solve.libs.io

import kotlin.jvm.JvmName

expect fun fileUrl(path: String): Url

expect fun remoteUrl(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null): Url

expect fun parseUrl(string: String): Url
