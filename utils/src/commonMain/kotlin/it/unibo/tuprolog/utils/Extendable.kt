package it.unibo.tuprolog.utils

import kotlin.js.JsName

interface Extendable {
    @JsName("extensions")
    val extensions: MutableMap<String, Any>

    @Suppress("UNCHECKED_CAST")
    @JsName("getExtension")
    fun <T : Any> getExtension(name: String): T? = extensions[name] as T?

    fun setExtension(name: String, value: Any) {
        extensions[name] = value
    }

    @JsName("containsExtension")
    fun containsExtension(name: String): Boolean = name in extensions
}
