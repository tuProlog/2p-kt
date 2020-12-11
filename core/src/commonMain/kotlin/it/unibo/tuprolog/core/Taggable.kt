package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.MissingTagExtension
import kotlin.js.JsName

interface Taggable<Self : Taggable<Self>> {
    @JsName("tags")
    val tags: Map<String, Any>

    @Suppress("UNCHECKED_CAST")
    @JsName("getTag")
    fun <T> getTag(name: String): T = tags[name].let { it as T } ?: throw MissingTagExtension(this, name)

    @JsName("tag")
    fun tag(name: String, value: Any): Self
}